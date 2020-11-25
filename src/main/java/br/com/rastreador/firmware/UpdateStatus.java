package br.com.rastreador.firmware;

public interface UpdateStatus {
    void onSucess();
    void onError(String error);
}
