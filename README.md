# CarService-AOSP

**CarService-AOSP** is a CarService implementation for standard AOSP that brings Android Automotive features to devices not built on Android Automotive OS. It lets car apps such as Google Maps Automotive run on AOSP, starting with Android 10 (API 29).

## Overview

Android Automotive OS ships with `CarService`, the system service that exposes the Car API (`android.car.*`) to apps. Standard AOSP builds, such as phones, tablets, and generic head units, do not include it, so apps that depend on the Car API cannot run there.

CarService-AOSP fills that gap. It provides a CarService that can be added to a regular AOSP build, so automotive apps like Google Maps Automotive can find the services they expect.

## Supported Versions

| AOSP version | API level | Status        |
|--------------|-----------|---------------|
| Android 10   | 29        | In development |
| Android 11+  | 30+       | Planned       |

## Download
Telegram: [https://t.me/GStore000](https://t.me/GStore000)

Note: You need to patch the APK yourself using https://github.com/kangrio/CarService-patches. The APK must have a minSdkVersion of 29 or lower.

## Usage

Follow these steps in the exact order:

1. **Install CarService** on your device.
2. **Install Google App Host**.
3. Open CarService and complete the initial setup.
4. Install any automotive-patched apps you want to use (e.g. Google Maps Automotive, Waze Automotive, PlugShare Automotive, or ABRP Automotive).
5. Allow **all installed apps** (CarService, Google App Host, and automotive apps) to run automatically at startup (Autostart).

**Important:** The installation and setup order must be followed exactly. Do not skip or rearrange the steps.

## Runnable Apps

The following Android Automotive apps have been tested and confirmed to run with CarService-AOSP:

- Google Maps Automotive
- Waze Automotive
- PlugShare Automotive
- ABRP (A Better Routeplanner) Automotive

**Other apps:** Compatibility has not been verified. Feel free to install and test other Android Automotive apps yourself.


## Features

- CarService running on standard (non-Automotive) AOSP
- Car API surface (`android.car.*`) for automotive apps
- Support for running Google Maps Automotive
- Built against AOSP Android 10 (API 29)

## BYD Mate Webhook Configuration

CarService-AOSP supports receiving live vehicle telemetry from BYD Mate through its local HTTP webhook.

### Configuration

1. Open **BYD Mate**.
2. Navigate to **Settings → Integrate → Webhook**.
3. Turn on **Webhook**.
4. Enter the following webhook URL:

   ```text
   127.0.0.1:58765/webhook
   ```

5. Save the configuration.

Once enabled, BYD Mate will send vehicle telemetry to CarService-AOSP automatically.

## Disclaimer

This is an independent, community project. It is not affiliated with, endorsed by, or sponsored by Google LLC. "Android", "Android Automotive OS", and "Google Maps" are trademarks of Google LLC. This software is provided for development and research purposes, and it is not certified for use in production vehicles or safety-critical systems.

## License

Copyright (C) 2026 KangRio

This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.

See the [LICENSE](LICENSE) file for the full license text.
