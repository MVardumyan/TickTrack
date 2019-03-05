<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<link href="//maxcdn.bootstrapcdn.com/bootstrap/3.3.0/css/bootstrap.min.css" rel="stylesheet" id="bootstrap-css">
<script src="//maxcdn.bootstrapcdn.com/bootstrap/3.3.0/js/bootstrap.min.js"></script>
<script src="//code.jquery.com/jquery-1.11.1.min.js"></script>
<style>
    .container{
        margin-top: 100px;
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

</style>

<div>
    <nav class="navbar navbar-expand-lg navbar-light bg-light">
    <a class="navbar-brand" href="#">T!ckTrack</a>
</nav>
<div class="container">
    <div class="row">

        <div class="col-md-7 ">

            <div class="panel panel-default">
                <div class="panel-heading">  <h4 >User Profile</h4></div>
                <div class="panel-body">
                    <div class="box box-info">
                        <div class="box-body">
                                <br>
                                <!-- /input-group -->
                            </div>
                            <div class="col-sm-6">
                                <h4 style="color:#00b1b1;"><c> ${username} </c></h4></span>
                                <span><p>${role}</p></span>
                            </div>
                            <div class="clearfix"></div>
                            <hr style="margin:5px 0 5px 0;">


                            <div class="col-sm-5 col-xs-6 tital " >First Name:</div><div class="col-sm-7 col-xs-6 ">${firstName}</div>
                            <div class="clearfix"></div>
                            <div class="bot-border"></div>


                            <div class="col-sm-5 col-xs-6 tital " >Last Name:</div><div class="col-sm-7"> ${lastName}</div>
                            <div class="clearfix"></div>
                            <div class="bot-border"></div>

                            <div class="col-sm-5 col-xs-6 tital " >Gender:</div><div class="col-sm-7"> ${gender}</div>
                            <div class="clearfix"></div>
                            <div class="bot-border"></div>

                            <div class="col-sm-5 col-xs-6 tital " >Email:</div><div class="col-sm-7"> ${email}</div>
                            <div class="clearfix"></div>
                            <div class="bot-border"></div>

                            <div class="col-sm-5 col-xs-6 tital " >Registration time:</div><div class="col-sm-7"> ${regTime}</div>
                            <div class="clearfix"></div>
                            <div class="bot-border"></div>

                            <div class="col-sm-5 col-xs-6 tital " >Member of following groups:</div><div class="col-sm-7">${groups}</div>
                            <div class="clearfix"></div>
                            <div class="bot-border"></div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
</div>
</html>