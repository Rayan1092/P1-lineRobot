import network
import usocket
import utime
from machine import Pin, PWM

''' HERE I make the actual serverSocket and establish the config and brodcast it'''
acp = network.WLAN(network.AP_IF)
acp.config(ssid = "RobotWIFI", password = "1234", security = 3)
acp.active(True)

''' using ipv4 address and TCP not UDP and accepting all connections on port 5000
and listening to one device and '''
server = usocket.socket(usocket.AF_INET, usocket.SOCK_STREAM)
server.bind(("0.0.0.0", 5000))
server.listen(1)
soc, ip = server.accept()

sensarr = []

sp = 0
Sens1 = Pin(21, Pin.IN)
Sens2 = Pin(10, Pin.IN)
Sens3 = Pin(20, Pin.IN)
Sens4 = Pin(11, Pin.IN)
Sens5 = Pin(19, Pin.IN)

Sensarr = [Sens1, Sens2, Sens3, Sens4, Sens5]

weights = [-2, -1, 0, 1, 2]


Inte = 0
dt = 20 / 1000
prevErr = 0

Kp = 1
Ki = 0
Kd = 0

minS = int(65535 * 0.40)

ENA = PWM(Pin(15))
ENB = PWM(Pin(16))

''' Motor A and B Dir 1 and 2'''
AD1 = Pin(0, Pin.OUT)
AD2 = Pin(1, Pin.OUT)
BD1 = Pin(2, Pin.OUT)
BD2 = Pin(3, Pin.OUT)

AD1.value(1)
AD2.value(0)

BD1.value(1)
BD2.value(0)

''' This makes it such that it thorws an exception if nothing is found'''
soc.setblocking(False) 

while True:
    
    sumTop = 0
    sumBot = 0
    
    for i in range(5):
        sumTop += Sensarr[i].value() * weights[i]
        sumBot += Sensarr[i].value()
    
    if sumBot == 0:
        continue
    
    pv = sumTop / sumBot
    
    
    currErr = sp - pv
    
    try:
        data = soc.recv(1024).decode("utf-8")
    except:
        data = None
        
    if data != None:
        parts = data.strip().split(",")
        Kp = float(parts[1])
        Ki = float(parts[2])
        Kd = float(parts[3])
    
    P = Kp * currErr
    
    Inte = (Inte + (currErr * dt))
    
    D = Kd * ((currErr - prevErr) / dt)
    
    
    corr = P + (Ki * Inte) + D
    
    leftS = minS + corr
    if leftS > 65535:
         leftS = 65535
    elif leftS < 0:
        leftS = 0
    
    rightS = minS - corr
    if rightS > 65535:
        rightS = 65535
        
    elif rightS < 0:
        rightS = 0
        
    ENA.duty_u16(int(leftS))
    ENB.duty_u16(int(rightS))
    
    soc.send(f"D,{currErr},{pv},{corr}\n".encode("utf-8"))
    prevErr = currErr
    
    utime.sleep_ms(20)
    