<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Link Sent</title>
    <link href="https://stackpath.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css" rel="stylesheet"
          integrity="sha384-wvfXpqpZZVQGK6TAh5PVlGOfQNHSoD2xbE+QkPxCAFlNEevoEH3Sl0sibVcOQVnN" crossorigin="anonymous">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/4.1.3/css/bootstrap.css">
    <%--<link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css"--%>
          <%--integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T" crossorigin="anonymous">--%>
    <%--<script src="//maxcdn.bootstrapcdn.com/bootstrap/3.3.0/js/bootstrap.min.js"></script>--%>
    <%--<script src="//code.jquery.com/jquery-1.11.1.min.js"></script>--%>
    <style>
        .navigation {
            background-color: #f5821f;
        <c:if test="${admin}"> background-color: brown;
        </c:if> position: fixed;
            left: 0;
            right: 0;
            top: 0;
            z-index: 9999;
            height: 34px;
            display: flex;
            align-items: center;
            justify-content: space-between;
        }

        .sidnav {
            height: 200%;
            position: absolute;
            right: 86%;
            left: 0;
            top: 34px;
            background-color: #474c55;
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

        .sidnav-item:hover {
            text-decoration: none;
            color: #f5821f;
        }

        .sidnav-item:active {
            text-decoration: none;
            color:  #f5821f;
        }

        .glyphicon-search,
        .glyphicon-user {
            margin-right: 20px;
        }

        section {
            text-align: center;
            position: absolute;
            top: 50%;
            left: 50%;
            transform: translate(-50%, -50%);
        }

        .body {
            margin-top: 50px;
            display: flex;
            justify-content: center;
            align-items: center;
            width: 100%;
            height: 100%;
        }
    </style>
</head>

<body>
<div class="navigation">
    <span class="navigation-brand">T!ckTrack</span>

    <div class="navigation-action-button">
        <a href="/search"> <span class="glyphicon glyphicon-search"></span></a>
        <a href="/personalInfo"><span class="glyphicon glyphicon-user"></span></a>
        <a href="/logout"><span class="glyphicon glyphicon-log-out"></span></a>
    </div>
</div>
<div class="sidnav">
    <div class="sidnav-item-conteiner">
        <a href="/regUserMain" class="sidnav-item">HOME</a>
    </div>

    <div class="sidnav-item-conteiner">
        <a href="/myTickets/1/10" class="sidnav-item">MY TICKETS</a>
    </div>

    <div class="sidnav-item-conteiner">
        <a href="/newTicket" class="sidnav-item">NEW TICKET</a>
    </div>
    <c:if test="${admin}">
        <div class="sidnav-item-conteiner">
            <a href="/admin/userManagement" class="sidnav-item">USER MANAGEMENT</a>
        </div>

        <div class="sidnav-item-conteiner">
            <a href="/admin/categoryManagement" class="sidnav-item">CATEGORY MANAGEMENT</a>
        </div>

        <div class="sidnav-item-conteiner">
            <a href="/admin/groupManagement" class="sidnav-item">GROUP MANAGEMENT</a>
        </div>
    </c:if>
</div>
<div class="body">
    <div class="row">
        <div class="col-md-12">
            <div class="error-template">
                <h1>
                    Success</h1>
                <h2>
                    Done!</h2>
                <div class="error-details">
                    Link for password change is sent to your mail address
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>

