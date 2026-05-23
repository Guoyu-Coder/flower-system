import websocket
import json
import time

class ZCloudLEDController:
    def __init__(self, uid, key, device_addr):
        self.uid = uid
        self.key = key
        self.device_addr = device_addr
        self.server_url = "ws://api.zhiyun360.com:28080/ws"
        self.ws = None
        self.connected = False
        self.response_received = False
        self.last_response = None

    def on_open(self, ws):
        print("WebSocket连接已建立")
        self.connected = True

    def on_close(self, ws, close_status_code, close_msg):
        print(f"WebSocket连接关闭: {close_status_code} - {close_msg}")
        self.connected = False

    def on_message(self, ws, message):
        print(f"收到服务器响应: {message}")
        self.last_response = message
        self.response_received = True

    def on_error(self, ws, error):
        print(f"WebSocket错误: {error}")

    def connect(self):
        self.ws = websocket.WebSocketApp(
            self.server_url,
            on_open=self.on_open,
            on_close=self.on_close,
            on_message=self.on_message,
            on_error=self.on_error
        )
        
        run_thread = websocket._app.run_forever
        import threading
        self.ws_thread = threading.Thread(target=run_thread, args=(self.ws,))
        self.ws_thread.daemon = True
        self.ws_thread.start()
        
        time.sleep(2)
        if not self.connected:
            raise Exception("连接失败，请检查网络和服务器地址")

    def authenticate(self):
        if not self.connected:
            raise Exception("未连接到服务器")
        
        auth_data = {
            "method": "authenticate",
            "uid": self.uid,
            "key": self.key,
            "version": "0.1.0",
            "autodb": True
        }
        self.response_received = False
        self.last_response = None
        self.ws.send(json.dumps(auth_data))
        
        timeout = 10
        start_time = time.time()
        while not self.response_received and (time.time() - start_time) < timeout:
            time.sleep(0.1)
        
        if not self.response_received:
            raise Exception("认证超时")
        
        return self.last_response

    def control_led(self, r, g, b, brightness=1):
        if not self.connected:
            raise Exception("未连接到服务器")
        
        zxbee_data = f"{{V0={r},V1={g},V2={b},V3={brightness}}}"
        
        control_data = {
            "method": "control",
            "addr": self.device_addr,
            "data": zxbee_data
        }
        self.response_received = False
        self.last_response = None
        self.ws.send(json.dumps(control_data))
        
        timeout = 10
        start_time = time.time()
        while not self.response_received and (time.time() - start_time) < timeout:
            time.sleep(0.1)
        
        if not self.response_received:
            raise Exception("控制指令发送超时")
        
        return self.last_response

    def close(self):
        if self.ws:
            self.ws.close()
            self.ws_thread.join(timeout=5)

def main():
    UID = "726757832120"
    KEY = "VFEDUIMPW1VXVQQEYFtYU1pAS2U"
    DEVICE_ADDR = "01:12:4B:00:0D:2D:44:3A"
    
    controller = ZCloudLEDController(UID, KEY, DEVICE_ADDR)
    
    try:
        print("正在连接到智云平台...")
        controller.connect()
        
        print("正在进行身份认证...")
        auth_result = controller.authenticate()
        print(f"认证结果: {auth_result}")
        
        print("\n正在设置LED灯为红色 (R=255, G=0, B=0, 亮度=1)...")
        control_result = controller.control_led(r=255, g=0, b=0, brightness=1)
        print(f"控制结果: {control_result}")
        
        print("\n操作完成！")
        
    except Exception as e:
        print(f"错误: {e}")
    finally:
        controller.close()

if __name__ == "__main__":
    main()