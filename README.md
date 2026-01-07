# CI/CD for [AntoineHazebrouckOrg](https://github.com/AntoineHazebrouckOrg)

## Run this application locally

#### Set up the fake production env TODO some steps can be included in the prebuilt VM

1. Download [virtualbox](https://www.virtualbox.org/wiki/Downloads)
2. Download the pre-set up OS : https://fromsmash.com/vm-pour-antoine, and unzip it.
3. Open the OS into virtualbox and log in with the following credentials :
- user=root
- password=root

4. Configure Docker to listen on TCP

```shell
nano /lib/systemd/system/docker.service
```

Append this "-H tcp://0.0.0.0:2375" to the end of the line that starts with "ExecStart"

Shut down the VM

In the VM network settings, forward the port 2375 (VM) to 12375 (physical)

Start the VM


#### Run the application

In [docker-compose.yml](./docker-compose.yml), set the following variables :
- GITHUB_CICD_TOKEN
- GITHUB_WEBHOOK_SECRET

```shell
docker compose up --build
```

Open the app at [http://localhost:8080](http://localhost:8080)