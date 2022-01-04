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

After booting, the Pi will automatically connect to the specified wireless network and ssh access will be enabled. It is recommended to change the default ssh password and add an ssh key. This is especially useful to avoid entering a password each time you connect to the Pi or run a rake task using ssh.

The setup task can be used to install `faasd` and `watchdog` on the device. The rake tasks therefore connects using ssh, which requires the environment variable `RPI` to be set.

**PowerShell:**

```powershell
$env:RPI = '<IP or DNS name>' ; rake setup
```

**Bash:**

```bash
RPI="<IP or DNS name>" rake setup
```

## Install edge-connector

For the orchestration to find the target devices in the edge layer, the Raspberry Pi is required to run a specific software called edge-connector. Please refer to the [edge-connector repository](https://github.com/doetlingerlukas/edge-connector-rs) on how to install it on the Pi.

After the program has been started on the device, it will execute a benchmark routine to measure the devices performance. After that, it will print the message *"Edge-device ready for connection!"* to the command line, indicating that it's ready for connection. Until then Apollo will not be able to discover the device.

## Install OpenFaaS CLI

Install [faas-cli](https://github.com/openfaas/faas-cli) to you local device to deploy function to the Raspberry Pi. While this step can be circumvented by using the OpenFaaS web interface, it is recommended to use the `faas-cli`. You can skip this step if you already got `faas-cli` installed. Otherwise use one of the recommended options:

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

Please refer to the official repository of [faas-cli](https://github.com/openfaas/faas-cli) if you find that those options are not working for you.

## Deploy Functions

Connect to the OpenFaaS server using `faas-cli login`. A collection of functions is described using a dedicated `.yml` file. Some examples, including their functions, are located in `functions`. Optionally take a look at the Wiki on how to [deploy one of the provided example functions](https://github.com/doetlingerlukas/rpi-serverless/wiki/Deploy-a-Function).

The provided `Rakefile` can be used to connect to the gateway using `rake connect`. Make sure to properly set the environment variable `RPI` to the address of the Raspberry Pi.

The experiment runs the [Sentiment-Analysis workflow](https://github.com/Apollo-Workflows/Sentiment-Analysis) designed for the Apollo platform. Refer to the repository on how to build and deploy the functions for using them with OpenFaaS.

The functions have already been compiled and uploaded to DockerHub for use with Raspberry Pi's. `functions/sentiment-analysis.yml` utilizes those functions and can be used to deploy them using the `faas-cli`. Make sure that you are connected with the `faas-cli` and the environment variable is correctly set. Run the following command to deploy the functions:

```sh
rake deploy['sentiment-analysis']
```

or alternatively,

```sh
cd ./functions
faas-cli deploy -f sentiment-analysis.yml --gateway http://<address of Pi>:8080
```

This may take some time as the images have to be downloaded and started on the Pi. You can verify that all functions are available by looking at the OpenFaaS web interface, where all functions should be listed.

### Use function running in AWS or IBM

If you would like to integrate serverless function running in AWS or IBM cloud services into the experiment, you need to manually deploy them to the services and create mappings in the workflow. Therefore, the mappings for each function need to be added to the appropriate file (i.e. `src\main\resources\sentiment-analysis\mappings.json` for the default sentiment-analysis workflow).

Here is an example on how such an entry looks:

```json
{
  "functionType": "sentim-batch",
  "resources": [
    {
      "type": "Serverless",
      "properties": {
        "Uri": "<function uri>"
      }
    }
  ]
}
```

Please refer to the [Sentiment-Analysis workflow repository](https://github.com/Apollo-Workflows/Sentiment-Analysis) on instructions on how to deploy the functions to either AWS or IBM.

## Run Experiment

The experiment uses the [Apollo Platform](https://github.com/Apollo-Core) to discover and orchestrate tasks on one or more Raspberry Pi's. The full experiment can be run with

```sh
./gradlew run
```

For the experiment to work properly, the Raspberry Pi's must be reachable in the same network, as the machine starting the orchestration. [Apollo](https://github.com/Apollo-Core) is then able to find those devices if they are running the therefore designed [edge-connector](https://github.com/doetlingerlukas/edge-connector-rs) software. Please refer to the edge-connector project for instructions on how to deploy it to a Raspberry Pi. The given instructions are designed to work with the configurations done in the previous step.


## Deploy functions (optional)

Take a look at the Wiki on how to [deploy one of the provided functions](https://github.com/doetlingerlukas/rpi-serverless/wiki/Deploy-a-Function).
