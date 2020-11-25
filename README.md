# firmware-updater

Código de exemplo:

```java
FirmwareUpdater updater = new FirmwareUpdater(<ip-rastreador>,<firmware-rastreador>,
                new UpdateStatus() {
                    @Override
                    public void onSucess() {
                        System.out.println("Firmware update success!");                        
                    }

                    @Override
                    public void onError(String error) {
                        System.out.println("Firmware update error: " + error);                        
                    }
                });

        updater.initUpdate();
```
