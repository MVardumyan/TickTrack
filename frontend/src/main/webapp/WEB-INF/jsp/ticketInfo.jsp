<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<link href="//maxcdn.bootstrapcdn.com/bootstrap/3.3.0/css/bootstrap.min.css" rel="stylesheet" id="bootstrap-css">
<script src="//maxcdn.bootstrapcdn.com/bootstrap/3.3.0/js/bootstrap.min.js"></script>
<script src="//code.jquery.com/jquery-1.11.1.min.js"></script>
<style>

    .sidnav {
        height: 200%;
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

    .body {
        margin-left: 100px;
        margin-top: 50px;
        padding-left: 190px;
    }

    .container__header {
        margin-bottom: 30px;
    }

    .form-group .btn-group {
        width: 100% !important;
    }

    .form-group .btn-group .multiselect {
        width: 100% !important;
    }

    .navbar {
        background-color: #F55C00 !important;
        position: fixed;
        left: 0;
        right: 0;
        top: 0;
        z-index: 9999;
    }

    .navbar .navbar-brand {
        color: white;
        font-family: monospace;
        font-size: 20px;
    }

    input.hidden {
        position: absolute;
        left: -9999px;
    }

    #profile-image1 {
        cursor: pointer;

        width: 100px;
        height: 100px;
        border:2px solid #03b1ce ;}
    .tital{ font-size:16px; font-weight:500;}
    .bot-border{ border-bottom:1px #f8f8f8 solid;  margin:5px 0  5px 0}

    .button-group {
        display: flex;
    }

    .action-button {
        margin-right: 20px;
        background-color: #62CCE8;
        color: white;
        border: none;
        border-radius: 3px;
        width: 180px;
        height: 40px;
        font-size: 18px;
    }

</style>

<div>
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
            <a href="/myTickets" class="sidnav-item">MY TICKETS</a>
        </div>

        <div class="sidnav-item-conteiner">
            <a href="/newTicket" class="sidnav-item">NEW TICKET</a>
        </div>
    </div>
    <div class="body">
        <div class="row">




            <div class="col-md-7 ">

                <div class="panel panel-default">
                    <div class="panel-heading">  <h4 >Ticket Info</h4></div>
                    <div class="panel-body">
                        <div class="box box-info">
                            <div class="box-body">
                                <br>
                                <!-- /input-group -->
                            </div>
                            <div class="col-sm-6">
                                <h4 style="color:#00b1b1;"> ${info.ticketID} </h4></span>
                                <span><p>Created by: ${info.creator}</p></span>
                            </div>
                            <div class="clearfix"></div>
                            <hr style="margin:5px 0 5px 0;">


                            <div class="col-sm-5 col-xs-6 tital " >Summary:</div><div class="col-sm-7 col-xs-6 ">${info.summary}</div>
                            <div class="clearfix"></div>
                            <div class="bot-border"></div>


                            <div class="col-sm-5 col-xs-6 tital " >Description:</div><div class="col-sm-7"> ${info.description}</div>
                            <div class="clearfix"></div>
                            <div class="bot-border"></div>

                            <div class="col-sm-5 col-xs-6 tital " >Resolution:</div><div class="col-sm-7"> ${info.resolution}</div>
                            <div class="clearfix"></div>
                            <div class="bot-border"></div>

                            <div class="col-sm-5 col-xs-6 tital " >Status:</div><div class="col-sm-7"> ${info.status}</div>
                            <div class="clearfix"></div>
                            <div class="bot-border"></div>

                            <div class="col-sm-5 col-xs-6 tital " >Priority:</div><div class="col-sm-7"> ${info.priority}</div>
                            <div class="clearfix"></div>
                            <div class="bot-border"></div>

                            <div class="col-sm-5 col-xs-6 tital " >Category:</div><div class="col-sm-7"> ${info.category}</div>
                            <div class="clearfix"></div>
                            <div class="bot-border"></div>

                            <div class="col-sm-5 col-xs-6 tital " >Assignee:</div><div class="col-sm-7">${info.assignee}</div>
                            <div class="clearfix"></div>
                            <div class="bot-border"></div>

                            <div class="col-sm-5 col-xs-6 tital " >Group:</div><div class="col-sm-7">${info.group}</div>
                            <div class="clearfix"></div>
                            <div class="bot-border"></div>

                            <div class="col-sm-5 col-xs-6 tital " >Deadline:</div><div class="col-sm-7">${info.deadline}</div>
                            <div class="clearfix"></div>
                            <div class="bot-border"></div>

                            <div class="col-sm-5 col-xs-6 tital " >Open Date:</div><div class="col-sm-7">${info.openDate}</div>
                            <div class="clearfix"></div>
                            <div class="bot-border"></div>

                            <div class="col-sm-5 col-xs-6 tital " >Close Date:</div><div class="col-sm-7">${info.closeDate}</div>
                            <div class="clearfix"></div>
                            <div class="bot-border"></div>

                            <%--<div class="col-sm-5 col-xs-6 tital " >Comments:</div><div class="col-sm-7">${info.comments}</div>--%>
                            <%--<div class="clearfix"></div>--%>
                            <%--<div class="bot-border"></div>--%>
                        </div>
                        </div>
                        </div>
                        </div>
                    </div>
        <div class="button-group">
            <form action="http://google.com">
                <button class="action-button">Edit</button>
            </form>
            <form action="http://google.com">
                <button class="action-button">Close</button>
            </form>
            <form action="http://google.com">
                <button class="action-button">Cancel</button>
            </form>
        </div>
    </div>
</div>
</div>
</html>

