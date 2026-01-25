// 🔥 Use current host (works on Render + local)
let protocol = location.protocol === "https:" ? "wss://" : "ws://";
let socket = new WebSocket(protocol + location.host + "/chat");

socket.onopen = () => {
    console.log("WebSocket connected");
};

socket.onmessage = function(event) {
    let chat = document.getElementById("chat");
    let messages = document.getElementById("messages");

    console.log("Raw message:", event.data);

    let data;
    try {
        data = JSON.parse(event.data);
    } catch (e) {
        console.error("Invalid JSON:", event.data);
        return;
    }

    // LEFT: original
    let originalDiv = document.createElement("div");
    originalDiv.className = "message other";
    originalDiv.textContent = data.original;
    messages.appendChild(originalDiv);

    // RIGHT: translated
    let translatedDiv = document.createElement("div");
    translatedDiv.className = "message me";
    translatedDiv.textContent = data.translated;
    messages.appendChild(translatedDiv);

    chat.scrollTop = chat.scrollHeight;
};

socket.onerror = (e) => {
    console.error("WebSocket error", e);
};

function sendMessage() {
    let input = document.getElementById("msg");
    let message = input.value;

    if (!message.trim()) return;

    socket.send(message);

    input.value = "";
}
