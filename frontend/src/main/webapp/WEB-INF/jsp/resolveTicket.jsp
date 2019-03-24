<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html><link href="https://stackpath.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css" rel="stylesheet" integrity="sha384-wvfXpqpZZVQGK6TAh5PVlGOfQNHSoD2xbE+QkPxCAFlNEevoEH3Sl0sibVcOQVnN" crossorigin="anonymous">
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/4.1.3/css/bootstrap.css">
<link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css" integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T" crossorigin="anonymous">
<script src="//maxcdn.bootstrapcdn.com/bootstrap/3.3.0/js/bootstrap.min.js"></script>
<script src="//code.jquery.com/jquery-1.11.1.min.js"></script>
<style>
    .navigation {
        background-color: #f5821f;
    <c:if test="${admin}">
        background-color: brown;
    </c:if>
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

    .button {
        background-color: #62CCE8;
        border: none;
        color: #62CCE8;
        padding: 16px 32px;
        text-align: center;
        text-decoration: none;
        display: inline-block;
        font-size: 16px;
        margin: 4px 2px;
        transition-duration: 0.4s;
        cursor: pointer;
    }
    .button1 {
        background-color: white;
        color: black;
        border: 2px solid #62CCE8;
    }

    .button1:hover {
        background-color: #62CCE8;
        color: white;
    }

    .body {
        margin-top: 50px;
        display: flex;
        justify-content: center;
        align-items: center;
        width: 100%;
        height: 100%;
    }

    .form-group .btn-group {
        width: 100% !important;
    }

    .form-group .btn-group .multiselect {
        width: 100% !important;
    }
</style>

<body>
<div>
    <div class="navigation">
        <span class="navigation-brand">T!ckTrack</span>

        <div class="navigation-action-button">
            <span class="glyphicon glyphicon-search"></span>
            <span class="glyphicon glyphicon-user"></span>
            <span class="glyphicon glyphicon-log-out"></span>
        </div>
    </div>
    <div class="body">
        <div class="row">
            <div class="col-md-12">

                <form method="post" action="/resolveTicket/${id}">
                <div class="error-template">
                    <h1>
                        Are you sure you want to resolve Ticket ${id}?</h1>
                    <div class="error-template">
                        <div class="form-group">
                         <textarea type="text" class="form-control" name="resolution" rows="6"
                               placeholder="resolution"></textarea>
                        </div>

                        <div>
                                <button class="button button1">Resolve</button>

                        </div>
                        <div>
                            <form action="/ticketInfo/${id}">
                                <button class="button button1">Take Me Back</button>
                            </form>
                        </div>
                    </div>

                </div>
                </form>
            </div>
        </div>
    </div>
</div>
</body>
</html>