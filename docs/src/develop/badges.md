# Badges

## How To Create Badges

We use [Shields.io](https://shields.io/) to generate Badges.
Please use the [badge links section](#_badge_links) to document and update currently used badges.
Open the links and download the svg files and place them in the projects `assets/images` folder or use `curl` instead.

## Download Badges

Simply use `curl` to download badges by providing the `url` and `filename`, that starts with `badge`.

```bash
curl "link" -s -o badge-filename
```

## Badge Links

Latest release:

```bash
curl "https://img.shields.io/badge/Release-0.1.0-blueviolet.svg?style=flat"  -s -o badge-release-latest.svg
```

License:

```bash
curl "https://img.shields.io/badge/License-ISC-lightgrey.svg?style=flat" -s -o badge-license.svg
```

## License

Shields is licensed under *Creative Commons Zero v1.0 Universal* (as of 2022-02-23)
