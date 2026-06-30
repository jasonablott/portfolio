# Notes setting up compose

Installed homebrew packages: docker, docker-compose, docker-machine (maybe unused?), colima, qemu

`colima start -d 20` -- make a linux VM with docker setup with a virtual 20GB disk

```
Compose is a Docker plugin. For Docker to find the plugin, add "cliPluginsExtraDirs" to ~/.docker/config.json:
  "cliPluginsExtraDirs": [
      "/opt/homebrew/lib/docker/cli-plugins"
  ]
```


gradle publishImageToLocalRegistry

DNSMasq setup: https://gist.github.com/ogrrd/5831371

Redirect *.asd.msd.localhost to 127.0.0.1"
