function Login(){ 
var done=0; 
var usuario=document.login.usuario.value; 
var password=document.login.password.value; 
if (usuario=="monitor" && password=="monitor") { 
window.location="monitor.html"; 
} 
if (usuario=="admin" && password=="admin") { 
window.location="administrador.html"; 
} 

if (usuario=="USUARIO2" && password=="CONTRASEÑA2") { 
window.location="TU_PAGINA_WEB.HTML"; 
} 
if (usuario=="" && password=="") { 
  alert("digite un usuario y contraseña"); 
  cargar_imagen_random();
} 
 
} 
 
document.oncontextmenu = function(){return false} 
 