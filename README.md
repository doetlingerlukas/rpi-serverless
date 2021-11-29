# rpi-serverless

Using a Raspberry Pi as an edge-device for serverless function execution.

## Setup Pi

First setup the Raspberry Pi with `faasd` (a detailed guide can be found in the [Wiki](https://github.com/doetlingerlukas/rpi-serverless/wiki/Setup-Raspberry-Pi)).

As an alternative, a `Rakefile` is provided for convenience.

If you have a fresh installation of Raspbian OS, configure the `boot` partition using the following command. This has to be done whilst the boot media is still attached to the local machine.

```sh
rake volume[<path_to_volume>, <ssid>, <psk>]
```

Setting up more than one wireless network is currently not supported with this rake task. Please refer to the [Wiki](https://github.com/doetlingerlukas/rpi-serverless/wiki/Setup-Raspberry-Pi) for additional information on how to edit the `wpa_supplicant.conf`.

After booting, the Pi will automatically connect to the specified wireless network and ssh access will be enabled.

The setup task can be used to install `faasd` and `watchdog` on the device. The rake tasks therefore connects using ssh, which requires the environment variable `RPI` to be set.

**Powershell:**

```powershell
$env:RPI = '<IP or DNS name>' ; rake setup
```

**Bash:**

```bash
RPI="<IP or DNS name>"; rake setup
```

## Run Experiment

The experiment uses the [Apollo Platform](https://github.com/Apollo-Core) to orchestrate sample tasks on or more Raspberry Pi's. The following command is used to start the orchestration:

```sh
./gradlew run
```

For the experiment to work properly, the Raspberry Pi's must be reachable in the same network, as the machine starting the orchestration. [Apollo](https://github.com/Apollo-Core) is then able to find those devices if they are running the therefore designed [edge-connector](https://github.com/doetlingerlukas/edge-connector-rs) software. Please refer to the edge-connector project for instructions on how to deploy it to a Raspberry Pi. The given instructions are designed to work with the configurations done in the previous step.

## Install OpenFaaS CLI (optional)

This is an optional step and not required for running the experiment. Install `faas-cli` to connect to the `faasd` instance running on the Raspberry Pi.

**Windows:**

```cmd
scoop install faas-cli
```

**Linux and Mac OS:**

```sh
brew install faas-cli
```

or

```sh
curl -sSL https://cli.openfaas.com | sudo -E sh
```

Connect to the OpenFaaS server using `faas-cli login`. Every function has a dedicated `.yml` file located in `functions`.

The provided `Rakefile` can be used to connect to the gateway using `rake login[<IP or URL>]`. The command requires the OpenFaaS password for the admin user to be saved in `faas-key.txt`. Alternatively a custom key file can be specified using `rake login[<IP or URL>, <path to file>]`.

## Deploy functions (optional)

Take a look at the Wiki on how to [deploy one of the provided functions](https://github.com/doetlingerlukas/rpi-serverless/wiki/Deploy-a-Function).
