import ServerConstants
from subprocess import call, check_output


def _save_profile(conf, name):
    prof_file = open("/etc/netctl/" + name, "w")
    prof_file.write(conf)
    prof_file.close()


def setup_interface(SSID, password, encryption):
    profile = """
Interface=wlan0
Connection=wireless
Security={}
ESSID='{}'
Key='{}'
IP=dhcp
"""

    profile = profile.format(encryption, SSID, password)
    _save_profile(profile, "insight-wlan-new")

    if call(["netctl", "switch-to", "insight-wlan-new"]) == 0:
        _save_profile(profile, "insight-wlan-default")
        call(["netctl", "enable", "insight-wlan-default"])
        return 0
    else:
        return call(["netctl", "switch-to", "insight-wlan-default"])


def reassociate():
    call(["netctl", "start", "insight-wlan-new"])


def check_connectivity():
    pass


def get_wlan0_ip():
    out = check_output(["sh", "-c", "ip addr show wlan0 | grep 'inet[^6]' | awk '{ print $2 }'"])
    if len(out) == 0:
        return None
    return out[0:out.find('/')]


def get_eth0_ip():
    out = check_output(["sh", "-c", "ip addr show eth0 | grep 'inet[^6]' | awk '{ print $2 }'"])
    if len(out) == 0:
        return None
    return out[0:out.find('/')]
