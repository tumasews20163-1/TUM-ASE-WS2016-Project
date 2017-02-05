# -*- coding: utf-8 -*-
import datetime
import time
import RPi.GPIO as GPIO
import os, signal, subprocess
import requests
import csv


def scanQR():
    # mit display und 640x480 Auflösung
    #zbarcam=subprocess.Popen("zbarcam --raw --prescale=640x480 /dev/video0",  stdout=subprocess.PIPE, shell=True, preexec_fn=os.setsid)
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

#Saves data into cvs file
def tempSave(data):
    with open("output.csv", "a") as f:
        writer = csv.writer(f)
        writer.writerows(data)

        

def trySend():
    url = 'http://utility-node-147216.appspot.com/api/tutor'
    #reads data from csv file and saves into data list
    data = list(csv.reader(open("output.csv")))
     #loops through the data and trys to send each element of the list
    #if sucsessfull delete this element after sending else prints no connection 
    while (len(data) > 0):
        try:
            print("Send: " + str(data[0]))
            tmp=dict()
            tmp = dict(username=data[0][0], password=data[0][1], QRString= data[0][2], SessionID=data[0][3],Participation=data[0][4])
            
            print(tmp)
            r = requests.post(url,headers=tmp)
            print(r.content)
            del data[0]
        except Exception:
            print("no internet connecton!")
            break

#saves data not been send again to the csv file            
    with open("output.csv", "w") as f:
        writer = csv.writer(f)
        writer.writerows(data)




#asks for username and password of tutor
username = raw_input("Please, enter your username: ")
password = raw_input("Your password, please: ")            
#initialises buttons of rasp py
GPIO.setmode(GPIO.BCM)
GPIO.setup(23, GPIO.IN, pull_up_down=GPIO.PUD_UP)
GPIO.setup(24, GPIO.IN, pull_up_down=GPIO.PUD_UP)
data = []
tmp=[]

while True:
    input_state2 = GPIO.input(23)
    input_state3 = GPIO.input(24)

 #in case button 23 is pressed adds all parameters to a list and participation false           
#after it saves string in csv and try to send it
    if input_state2 == False:
        now = datetime.datetime.now()
        nowText = now.strftime("%d/%m/%y")
        plain = scanQR()
        #data.append(plain)
        print(plain)
        time.sleep(0.5)
	tmp.append(username)
	tmp.append(password)
  	tmp.append(plain)
	tmp.append(nowText)
	tmp.append("False")
	data.append(tmp)
	tmp=[]
        tempSave(data)
        data=[]
        trySend()
        
 #in case button 24 is pressed adds all parameters to a list and participation true
#after it saves string in csv and try to send it
    if input_state3 ==False:
        now = datetime.datetime.now()
        nowText = now.strftime("%d/%m/%y")
        plainT = scanQR()
	tmp.append(username)
	tmp.append(password)
  	tmp.append(plain)
	tmp.append(nowText)
	tmp.append("True")
	data.append(tmp)
	tmp=[]
        tempSave(data)
        data=[]
        trySend()
       
        
        


