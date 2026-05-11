

   var imagen_random = new Array();
    imagen_random[0] = "../assets/img/fondo01.svg";
    imagen_random[1] = "../assets/img/fondo02.svg";
    imagen_random[2] = "../assets/img/fondo03.svg";
    imagen_random[3] = "../assets/img/fondo04.svg";
    imagen_random[4] = "../assets/img/fondo05.svg"; 

    function cargar_imagen_random(){
       //document.images["random"].src = imagen_random[azar];
       document.querySelectorAll('#loginFondo').forEach(function (imagen) {
          var azar = Math.floor(Math.random() * imagen_random.length);
          imagen.src = imagen_random[azar];
       });
       //console.log('cargo script');
    }
