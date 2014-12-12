import ServerConstants
from subprocess import call


def setup_interface(SSID, password):
    N = "0"
    call(["wpa_cli", "set_network", N, "auth_alg", "OPEN"])
    call(["wpa_cli", "set_network", N, "key_mgmt", "NONE"])
    call(["wpa_cli", "set_network", N, "mode", "0"])
    call(["wpa_cli", "set_network", N, "ssid", '"'+SSID+'"'])
    call(["wpa_cli", "set_network", N, "wep_key0", '"'+password+'"'])
    call(["wpa_cli", "reassociate"])
    call(["wpa_cli", "save_config"])


def check_connectivity():
    pass

