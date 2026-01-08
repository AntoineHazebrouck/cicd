# CI/CD for [AntoineHazebrouckOrg](https://github.com/AntoineHazebrouckOrg)

## Run this application locally

### Set up the fake production env TODO some steps can be included in the prebuilt VM

1. Download [virtualbox](https://www.virtualbox.org/wiki/Downloads)
2. Download the pre-set up OS : https://fromsmash.com/vm-pour-antoine, and unzip it.
3. Open the OS into virtualbox and log in with the following credentials :
- user=root
- password=root

4. Configure Docker to be exposed on TCP

```shell
nano /lib/systemd/system/docker.service
```

Append this "-H tcp://0.0.0.0:2375" to the end of the line that starts with "ExecStart"

Shut down the VM

In the VM network settings, forward the port 2375 (VM) to 12375 (physical)

Start the VM

5. Make sure to expose the prod app

In the VM network settings, forward the port 8080 (VM) to 18080 (physical)

The prod application you run has to be exposing port 8080

### Set up tokens

```shell
docker compose up --build
```

Open sonar at [http://localhost:9000](http://localhost:9000) and login :
- username=admin
- password=admin

Go to My account > Security, generate a "Global Analysis Token"

In [docker-compose.yml](./docker-compose.yml), set the following variables :
- SONAR_TOKEN="the token you generated"
- GITHUB_CICD_TOKEN="ask the github admin"
- GITHUB_WEBHOOK_SECRET="ask the github admin"

### Set up the tunnel

We need to use a tunnel so that our webhook can contact the application locally.

1. Install [cloudflare tunnel](https://github.com/cloudflare/cloudflared/releases/latest/download/cloudflared-windows-amd64.exe)
2. Open a terminal and launch the command :
```shell
cloudflared tunnel --url http://127.0.0.1:8080/
```
to create a tunnel through which the webhook will contact the app.

3. After the command is executed, a url will appear. Copy it.
4. In the Github repository of your app, go to Settings > Webhooks
5. Click edit
6. Paste the url you copied in **Payload URL \*** and add /api/webhooks/push at the end
7. Click Update webhook

### Run the application

```shell
docker compose up --build
```

Open the app at [http://localhost:8080](http://localhost:8080)

### Schemas

[local setup](./docs/local-setup-schema.png)
[pipeline](./docs/pipeline-schema.png)