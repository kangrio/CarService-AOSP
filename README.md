# CarService-AOSP

**CarService-AOSP** is a CarService implementation for standard AOSP that brings Android Automotive features to devices not built on Android Automotive OS. It lets car apps such as Google Maps Automotive run on AOSP, starting with Android 10 (API 29).

## Overview

Android Automotive OS ships with `CarService`, the system service that exposes the Car API (`android.car.*`) to apps. Standard AOSP builds, such as phones, tablets, and generic head units, do not include it, so apps that depend on the Car API cannot run there.

CarService-AOSP fills that gap. It provides a CarService that can be added to a regular AOSP build, so automotive apps like Google Maps Automotive can find the services they expect.

## Download
Telegram: [https://t.me/GStore000/10](https://t.me/GStore000/10)

## Features

- CarService running on standard (non-Automotive) AOSP
- Car API surface (`android.car.*`) for automotive apps
- Support for running Google Maps Automotive
- Built against AOSP Android 10 (API 29)

## Supported Versions

| AOSP version | API level | Status        |
|--------------|-----------|---------------|
| Android 10   | 29        | In development |
| Android 11+  | 30+       | Planned       |

## Disclaimer

This is an independent, community project. It is not affiliated with, endorsed by, or sponsored by Google LLC. "Android", "Android Automotive OS", and "Google Maps" are trademarks of Google LLC. This software is provided for development and research purposes, and it is not certified for use in production vehicles or safety-critical systems.

## License

Copyright (C) 2026 KangRio

This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.

See the [LICENSE](LICENSE) file for the full license text.
