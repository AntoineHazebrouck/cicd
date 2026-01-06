# CI/CD for [AntoineHazebrouckOrg](https://github.com/AntoineHazebrouckOrg)

## Run this application locally

#### Set up the fake production env TODO some steps can be included in the prebuilt VM

1. Download [virtualbox](https://www.virtualbox.org/wiki/Downloads)
2. Download the pre-set up OS : https://fromsmash.com/vm-pour-antoine, and unzip it.
3. Open the OS into virtualbox and log in with the following credentials :
- user=user
- password=user
4. Authenticate as root : 

```shell
su -
```

The password is "root"

5. Find the VM's ip address : 

```shell
ip addr show
```

Look for enp0s3

10.0.2.15

6. Configure Docker to listen on TCP

```shell
nano /lib/systemd/system/docker.service
```

Append this "-H tcp://0.0.0.0:2375" to the line that starts with "ExecStart"

Restart Docker :

```shell
systemctl daemon-reload
systemctl restart docker
```


#### Run the application

In [docker-compose.yml](./docker-compose.yml), set the following variables :
- GITHUB_CICD_TOKEN
- GITHUB_WEBHOOK_SECRET

```shell
docker compose up --build
```

Open the app at [http://localhost:8080](http://localhost:8080)