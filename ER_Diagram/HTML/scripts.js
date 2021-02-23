window.onload = function () {
    var nodes = document.querySelectorAll('[data-connects]');
    for (var node of nodes) {
        var conn1 = node.dataset.connects.split(" ")[0];
        var conn2 = node.dataset.connects.split(" ")[1];
        alert("connection between " + conn1 + " and " + conn2);
    }
}
