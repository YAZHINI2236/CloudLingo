
let protocol = location.protocol === "https:" ? "wss://" : "ws://";
let socket = new WebSocket(protocol + location.host + "/chat");

socket.onopen = () => {
    console.log("WebSocket connected");
};

socket.onmessage = function(event) {
    let data = JSON.parse(event.data);

    let messages = document.getElementById("messages");
    let chat = document.getElementById("chat");

    let msgDiv = document.createElement("div");

    msgDiv.className = "message other";
    msgDiv.textContent = data.translated;

    messages.appendChild(msgDiv);
    chat.scrollTop = chat.scrollHeight;
};

socket.onerror = (e) => {
    console.error("WebSocket error", e);
};

function sendMessage() {
    let input = document.getElementById("msg");
    let message = input.value;

    if (!message.trim()) return;

    let messages = document.getElementById("messages");
    let chat = document.getElementById("chat");

    let msgDiv = document.createElement("div");
    msgDiv.className = "message me";
    msgDiv.textContent = message;

    messages.appendChild(msgDiv);
    chat.scrollTop = chat.scrollHeight;

    socket.send(message);

    input.value = "";
}
