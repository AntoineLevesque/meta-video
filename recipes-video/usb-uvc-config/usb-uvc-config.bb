SUMMARY = "bitbake-layers recipe"
DESCRIPTION = "Recipe created by bitbake-layers"
LICENSE = "CLOSED"

SCRIPT_FILE = "${THISDIR}/files/usb-gadget-setup.sh"

FILES:${PN} += "${sysconfdir}/cron.reboot"
FILES:${PN} += "${sysconfdir}/cron.reboot/usb-gadget-setup.sh"

do_install(){
    install -d ${D}${sysconfdir}/cron.reboot
    install -m 0755 ${SCRIPT_FILE} ${D}${sysconfdir}/cron.reboot
}
