package xc.lib.host;

public interface PluginInstallListener {


    void onInstallSuccess(PluginInfo pi);
    void onInstallFailure(Exception e,PluginInfo pi);

}
