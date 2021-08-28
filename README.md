# rpi-serverless

Using a Raspberry Pi as an edge-device for serverless function execution.

## Setup Pi

First setup the Raspberry Pi with `faasd` (a detailed guide can be found in the [Wiki](https://github.com/doetlingerlukas/rpi-serverless/wiki/Setup-Raspberry-Pi)).

As an alternative, a `Rakefile` is provided for convenience.

If you have a fresh installation of Raspbian OS, configure the `boot` partition using:

```sh
rake volume['<path_to_volume>', '<ssid>', '<psk>']
```

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

## Install OpenFaaS CLI

Install `faas-cli` to connect to the `faasd` instance running on the Raspberry Pi.

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

## Deploy functions

Take a look at the Wiki on how to [deploy one of the provided functions](https://github.com/doetlingerlukas/rpi-serverless/wiki/Deploy-a-Function).
