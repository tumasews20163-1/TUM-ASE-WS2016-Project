# -*- coding: utf-8 -*-
import datetime
import time
import RPi.GPIO as GPIO
import os, signal, subprocess


def scanQR():
    # mit display und 640x480 Auflösung
    #zbarcam=subprocess.Popen("zbarcam --raw --prescale=640x480 /dev/video0", stdout=subprocess.PIPE, shell=True, preexec_fn=os.setsid)
    # mit Display und max. Auflösung
    #zbarcam=subprocess.Popen("zbarcam --raw /dev/video0", stdout=subprocess.PIPE, shell=True, preexec_fn=os.setsid)
    # ohne Display und max. Auflösung
    qrcam=subprocess.Popen("zbarcam --raw --nodisplay /dev/video0", stdout=subprocess.PIPE, shell=True, preexec_fn=os.setsid)
    print 'QR-Code wird gescannt'
    while True:
        plaintext=qrcam.stdout.readline().split('\n')
        if plaintext!='':
            print 'QR scannen erfolgreich!'
            break
    os.killpg(qrcam.pid, signal.SIGTERM)  # Prozess stoppen
    print 'QR-scan beendet'
    return plaintext[0]



GPIO.setmode(GPIO.BCM)
GPIO.setup(18, GPIO.IN, pull_up_down=GPIO.PUD_UP)
GPIO.setup(23, GPIO.IN, pull_up_down=GPIO.PUD_UP)
GPIO.setup(24, GPIO.IN, pull_up_down=GPIO.PUD_UP)
data = []
participant = False

while True:
    input_state1 = GPIO.input(18)
    input_state2 = GPIO.input(23)
    input_state3 = GPIO.input(24)

    if input_state1 == False:
        now = datetime.datetime.now()
        nowText = now.strftime("%d/%m/%y")
        #data.append(now.strftime("%d/%m/%y"))
        print(nowText)
        time.sleep(0.5)
        
    if input_state2 == False:
        now = datetime.datetime.now()
        nowText = now.strftime("%d/%m/%y")
        plain = scanQR()
        #data.append(plain)
        print(plain)
        time.sleep(0.5)
        data.append(nowText + " " + plain + " part: " + str(participant))
        print(data)

    if input_state3 ==False:
        now = datetime.datetime.now()
        nowText = now.strftime("%d/%m/%y")
        participant = True
        plainT = scanQR()
        data.append(nowText + " " + plainT + " part: " + str(participant))
        print(data)
        
        



