<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
<style>
   .sidnav {
       height: 100%;
       position: absolute;
       right: 86%;
       left: 0;
       top: 34px;
       background-color: #474c55;
   }

   .navigation {
       background-color: #f5821f;
       position: fixed;
       left: 0;
       right: 0;
       top: 0px;
       z-index: 9999;
       height: 34px;
       display: flex;
       align-items: center;
       justify-content: space-between;
   }

   .navigation .navigation-brand {
       color: white;
       font-family: monospace;
       font-size: 20px;
       margin-left: 20px;
   }

   .navigation-action-button {
       margin-right: 20px;
   }

   .sidnav-item-conteiner {
       border-bottom: 1px solid white;
       padding: 20px;
   }

   .sidnav-item {
       text-decoration: none;
       font-size: 17px;
       text-transform: uppercase;
       color: white;
       font-family: monospace;
   }

    .glyphicon-search,
    .glyphicon-user {
        margin-right: 20px;
    }

</style>

<body>
    <div class="navigation">
        <span class="navigation-brand">T!ckTrack</span>

        <div class="navigation-action-button">
            <a href="http://localhost:9093/search"> <span class="glyphicon glyphicon-search"></span></a>
            <a href="http://localhost:9093/personalInfo"><span class="glyphicon glyphicon-user"></span></a>
            <a href="http://localhost:9093/login"><span class="glyphicon glyphicon-log-out"></span></a>
        </div>
    </div>
    <div class="sidnav">
        <div class="sidnav-item-conteiner">
            <a href="http://localhost:9093/regUserMain" class="sidnav-item">HOME</a>
        </div>

        <div class="sidnav-item-conteiner">
            <a href="http://localhost:9093/ticketInfo" class="sidnav-item">MY TICKETS</a>
        </div>

        <div class="sidnav-item-conteiner">
            <a href="#" class="sidnav-item">NEW TICKET</a>
        </div>

        <div class="sidnav-item-conteiner">
            <a href="#" class="sidnav-item">UPDATE TICKET</a>
        </div>

        <div class="sidnav-item-conteiner">
            <a href="#" class="sidnav-item">CHANGE PASSWORD</a>
        </div>

    </div>
</body>
</html>