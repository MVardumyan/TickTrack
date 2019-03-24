<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<link href="https://stackpath.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css" rel="stylesheet"
      integrity="sha384-wvfXpqpZZVQGK6TAh5PVlGOfQNHSoD2xbE+QkPxCAFlNEevoEH3Sl0sibVcOQVnN" crossorigin="anonymous">
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/4.1.3/css/bootstrap.css">
<link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css"
      integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T" crossorigin="anonymous">
<script src="//maxcdn.bootstrapcdn.com/bootstrap/3.3.0/js/bootstrap.min.js"></script>
<script src="//code.jquery.com/jquery-1.11.1.min.js"></script>
<style>
    .navigation {
        background-color: #f5821f;
    <c:if test="${admin}"> background-color: brown;
    </c:if> position: fixed;
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
                <div class="error-template">
                    <c:if test="${close}">
                        <h1>
                            Are you sure you want to close Ticket ${id}?</h1>
                        <div>
                            <form method="post" action="/closeTicket/${id}">
                                <button class="button button1">Close</button>
                            </form>
                        </div>
                    </c:if>
                    <c:if test="${cancel}">
                    <div class="error-template">
                        <h1>
                            Are you sure you want to cancel Ticket ${id}?</h1>
                        <div>
                            <form method="post" action="/cancelTicket/${id}">
                                <button class="button button1">Cancel</button>
                            </form>
                        </div>
                        </c:if>
                        <c:if test="${progress}">
                        <div class="error-template">
                            <h1>
                                Are you sure you want to change Ticket ${id}'s status to in progress?</h1>
                            <div>
                                <form method="post" action="/progressTicket/${id}">
                                    <button class="button button1">Yes</button>
                                </form>
                            </div>
                            </c:if>
                            <div>
                                <form action="/ticketInfo/${id}">
                                    <button class="button button1">Take Me Back</button>
                                </form>
                            </div>
                        </div>

                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>