#! /bin/sh
  modprobe libcomposite

  cd /sys/kernel/config/
  cd usb_gadget
  mkdir g1
  cd g1

  echo "0x04D8" > idVendor
  echo "0x1234" > idProduct
  
  mkdir strings/0x409
  echo "0123456789" > strings/0x409/serialnumber
  echo "Microchip Technology, Inc." > strings/0x409/manufacturer
  echo "Linux USB Gadget" > strings/0x409/product

  mkdir functions/uvc.usb0
  mkdir configs/c.1
  mkdir configs/c.1/strings/0x409
  echo "UVC" > configs/c.1/strings/0x409/configuration

  # Camera related configurations
  mkdir -p functions/uvc.usb0/streaming/uncompressed/u/360p
  echo "666666" >> functions/uvc.usb0/streaming/uncompressed/u/360p/dwFrameInterval
  echo "1000000" >> functions/uvc.usb0/streaming/uncompressed/u/360p/dwFrameInterval
  echo "5000000" >> functions/uvc.usb0/streaming/uncompressed/u/360p/dwFrameInterval

  # Create header
  mkdir functions/uvc.usb0/streaming/header/h
  cd functions/uvc.usb0/streaming/header/h
  ln -s ../../uncompressed/u
  cd ../../class/fs
  ln -s ../../header/h
  cd ../../class/hs
  ln -s ../../header/h
  cd ../../class/ss
  ln -s ../../header/h
  cd ../../../control
  mkdir -p header/h
  ln -s header/h class/fs
  ln -s header/h class/ss
  cd ../../../

  echo 2048 > functions/uvc.usb0/streaming_maxpacket
  ln -s functions/uvc.usb0 configs/c.1
  echo "300000.gadget" > UDC