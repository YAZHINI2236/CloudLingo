let socket = new WebSocket("ws://localhost:8080/chat");

socket.onmessage = function(event) {
    let chat = document.getElementById("chat");
    let messages = document.getElementById("messages");
    let decoded = decodeURIComponent(event.data);

    let msgDiv = document.createElement("div");
    msgDiv.className = "message other";   // LEFT
    msgDiv.textContent = decoded;

    messages.appendChild(msgDiv);

    // Always scroll to bottom
    chat.scrollTop = chat.scrollHeight;
};

function sendMessage() {
    let input = document.getElementById("msg");
    let message = input.value;

    if (!message.trim()) return;

    socket.send(message);

    let chat = document.getElementById("chat");
    let messages = document.getElementById("messages");

    let msgDiv = document.createElement("div");
    msgDiv.className = "message me";   // RIGHT
    msgDiv.textContent = message;

    messages.appendChild(msgDiv);

    chat.scrollTop = chat.scrollHeight;

    input.value = "";
}
