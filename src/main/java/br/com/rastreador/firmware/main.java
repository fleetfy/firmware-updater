package br.com.rastreador.firmware;

public class main {

    public static void main(String[] args) throws Throwable {
         FirmwareUpdater updater = new FirmwareUpdater("192.168.15.5",
                 "C:\\Users\\barros\\Documents\\projetos\\rastreador\\firmware\\.pio\\build\\debug\\firmware.bin",
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
    }

}
