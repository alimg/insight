import ServerConstants
from subprocess import call, check_output


def setup_interface(SSID, password, encryption):
    N = "0"
    call(["wpa_cli", "set_network", N, "auth_alg", "OPEN"])
    if encryption == "WEP":
        call(["wpa_cli", "set_network", N, "key_mgmt", "NONE"])
        call(["wpa_cli", "set_network", N, "wep_key0", '"' + password + '"'])
    elif encryption == "WPA-PSK":
        call(["wpa_cli", "set_network", N, "key_mgmt", "WPA-PSK"])
        call(["wpa_cli", "set_network", N, "psk", '"' + password + '"'])
        call(["wpa_cli", "set_network", N, "pairwise", "CCMP", "TKIP"])
        call(["wpa_cli", "set_network", N, "group", "CCMP", "TKIP"])
    call(["wpa_cli", "set_network", N, "mode", "0"])
    call(["wpa_cli", "set_network", N, "ssid", '"' + SSID + '"'])
    call(["wpa_cli", "reassociate"])
    call(["wpa_cli", "save_config"])


def reassociate():
    call(["wpa_cli", "reassociate"])


def check_connectivity():
    pass


def get_wlan0_ip():
    out = check_output(["sh", "-c", "ip addr show wlan0 | grep inet | awk '{ print $2 }'"])
    if len(out) == 0:
        return None
    return out[0:out.find('/')]
