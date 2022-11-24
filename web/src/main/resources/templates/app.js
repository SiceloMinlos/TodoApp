document.addEventListener("DOMContentLoaded", () => {

    var url = "http://localhost:7000/provinces";

    var xhr = new XMLHttpRequest();
    xhr.open("GET", url);

    xhr.onreadystatechange = function () {
       if (xhr.readyState === 4) {
          console.log(xhr.status);
          console.log(xhr.responseText);
       }};

});