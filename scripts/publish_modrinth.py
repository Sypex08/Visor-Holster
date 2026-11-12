import json
import os
import re
import sys
import urllib.error
import urllib.parse
import urllib.request
from pathlib import Path


API_ROOT = "https://api.modrinth.com/v2"
VISOR_PROJECT_ID = "p5sM2GSi"
USER_AGENT = "Sypex08/VisorHolster-GitHub-Actions (https://github.com/Sypex08/Visor-Holster)"
LOADER_NAMES = {
    "fabric": "Fabric",
    "forge": "Forge",
    "neoforge": "Neoforge",
}


def request_json(url, token=None):
    headers = {"User-Agent": USER_AGENT}
    if token:
        headers["Authorization"] = token
    request = urllib.request.Request(url, headers=headers)
    try:
        with urllib.request.urlopen(request, timeout=60) as response:
            return json.loads(response.read())
    except urllib.error.HTTPError as error:
        detail = error.read().decode("utf-8", errors="replace")
        raise RuntimeError(f"Modrinth API returned HTTP {error.code}: {detail}") from error


def create_version(token, metadata, jar_path):
    boundary = "----VisorHolsterModrinthUploadBoundary"
    filename = jar_path.name
    body = bytearray()

    body.extend(f"--{boundary}\r\n".encode())
    body.extend(b'Content-Disposition: form-data; name="data"\r\n')
    body.extend(b"Content-Type: application/json\r\n\r\n")
    body.extend(json.dumps(metadata, separators=(",", ":")).encode("utf-8"))
    body.extend(b"\r\n")

    body.extend(f"--{boundary}\r\n".encode())
    body.extend(
        f'Content-Disposition: form-data; name="file"; filename="{filename}"\r\n'.encode()
    )
    body.extend(b"Content-Type: application/java-archive\r\n\r\n")
    body.extend(jar_path.read_bytes())
    body.extend(b"\r\n")
    body.extend(f"--{boundary}--\r\n".encode())

    request = urllib.request.Request(
        f"{API_ROOT}/version",
        data=bytes(body),
        headers={
            "Authorization": token,
            "User-Agent": USER_AGENT,
            "Content-Type": f"multipart/form-data; boundary={boundary}",
        },
        method="POST",
    )
    try:
        with urllib.request.urlopen(request, timeout=300) as response:
            return json.loads(response.read())
    except urllib.error.HTTPError as error:
        detail = error.read().decode("utf-8", errors="replace")
        raise RuntimeError(
            f"Failed to upload {filename}: HTTP {error.code}: {detail}"
        ) from error


def main():
    token = os.environ.get("MODRINTH_TOKEN", "").strip()
    project_id = os.environ.get("MODRINTH_PROJECT_ID", "").strip()
    tag = os.environ.get("MODRINTH_RELEASE_TAG", "").strip() or os.environ.get("GITHUB_REF_NAME", "").strip()
    repository = os.environ.get("GITHUB_REPOSITORY", "Sypex08/Visor-Holster")
    server_url = os.environ.get("GITHUB_SERVER_URL", "https://github.com").rstrip("/")

    if not token:
        raise RuntimeError("Missing MODRINTH_TOKEN GitHub Actions secret.")
    if not project_id:
        raise RuntimeError("Missing MODRINTH_PROJECT_ID GitHub Actions secret.")
    if not tag.startswith("v"):
        raise RuntimeError(f"Expected a release tag beginning with 'v', got {tag!r}.")

    release_version = tag[1:]
    if not re.fullmatch(r"[A-Za-z0-9][A-Za-z0-9._+-]{0,31}", release_version):
        raise RuntimeError(f"Tag version is not a valid Modrinth version: {release_version!r}")

    assets_dir = Path("release-assets")
    prefix = f"VisorHolster-{release_version}-"
    jars = sorted(assets_dir.glob("*.jar"))
    if not jars:
        raise RuntimeError(f"No JARs found in {assets_dir}.")

    artifacts = []
    for jar_path in jars:
        if not jar_path.name.startswith(prefix):
            raise RuntimeError(f"Unexpected release asset name: {jar_path.name}")
        suffix = jar_path.stem[len(prefix):]
        loader_name, separator, game_version = suffix.partition("-")
        loader = loader_name.lower()
        if not separator or loader not in LOADER_NAMES:
            raise RuntimeError(f"Could not parse loader and Minecraft version from {jar_path.name}")
        if not re.fullmatch(r"[0-9]+(?:\.[0-9]+)*", game_version):
            raise RuntimeError(f"Invalid Minecraft version in asset name: {jar_path.name}")

        version_number = f"{release_version}+mc{game_version}-{loader}"
        if len(version_number) > 32:
            raise RuntimeError(
                f"Generated Modrinth version number is too long: {version_number}"
            )
        artifacts.append((jar_path, game_version, loader, version_number))

    project_path = urllib.parse.quote(project_id, safe="")
    versions = request_json(
        f"{API_ROOT}/project/{project_path}/version?include_changelog=false",
        token,
    )
    existing_numbers = {version.get("version_number") for version in versions}

    release_url = f"{server_url}/{repository}/releases/tag/{urllib.parse.quote(tag, safe='')}"
    version_type = "release"
    lowered_version = release_version.lower()
    if "alpha" in lowered_version:
        version_type = "alpha"
    elif "beta" in lowered_version or "rc" in lowered_version:
        version_type = "beta"

    for jar_path, game_version, loader, version_number in artifacts:
        if version_number in existing_numbers:
            print(f"Already on Modrinth; skipping {version_number}")
            continue

        metadata = {
            "project_id": project_id,
            "name": (
                f"VisorHolster {release_version} - "
                f"{LOADER_NAMES[loader]} - Minecraft {game_version}"
            ),
            "version_number": version_number,
            "changelog": f"See the release notes: {release_url}",
            "dependencies": [
                {
                    "project_id": VISOR_PROJECT_ID,
                    "dependency_type": "required",
                }
            ],
            "game_versions": [game_version],
            "version_type": version_type,
            "loaders": [loader],
            "featured": False,
            "file_parts": ["file"],
            "primary_file": "file",
        }

        result = create_version(token, metadata, jar_path)
        existing_numbers.add(version_number)
        print(
            f"Uploaded {jar_path.name} as Modrinth version "
            f"{result.get('version_number', version_number)}"
        )

    print(f"Processed {len(artifacts)} JARs for {tag}.")


if __name__ == "__main__":
    try:
        main()
    except Exception as error:
        print(f"Modrinth publishing failed: {error}", file=sys.stderr)
        sys.exit(1)

