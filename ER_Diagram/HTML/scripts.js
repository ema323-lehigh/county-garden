window.onload = function () {
    var svg = document.querySelector("svg");
    var nodes = document.querySelectorAll('[data-connects]');
    for (var node of nodes) {
        var conn1 = document.getElementById(node.dataset.connects.split(" ")[0]);
        var conn2 = document.getElementById(node.dataset.connects.split(" ")[1]);
        var line1 = document.createElementNS("http://www.w3.org/2000/svg", "line");
        line1.setAttribute('id', node.dataset.connects.split(" ").join("-") + "-1");
        line1.setAttribute('x1', conn1.getBoundingClientRect().left);
        line1.setAttribute('y1', conn1.getBoundingClientRect().top);
        line1.setAttribute('x2', node.getBoundingClientRect().left);
        line1.setAttribute('y2', node.getBoundingClientRect().top);
        svg.appendChild(line1);
        var line2 = document.createElementNS("http://www.w3.org/2000/svg", "line");
        line2.setAttribute('id', node.dataset.connects.split(" ").join("-") + "-2");
        line2.setAttribute('x1', node.getBoundingClientRect().left);
        line2.setAttribute('y1', node.getBoundingClientRect().top);
        line2.setAttribute('x2', conn2.getBoundingClientRect().left);
        line2.setAttribute('y2', conn2.getBoundingClientRect().top);
        svg.appendChild(line2);
    }
}
