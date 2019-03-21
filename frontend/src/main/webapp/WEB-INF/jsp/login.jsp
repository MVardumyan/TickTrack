<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html>
<head>
    <title>Welcome to T!ckTrack</title>
    <link href="https://stackpath.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css" rel="stylesheet"
          integrity="sha384-wvfXpqpZZVQGK6TAh5PVlGOfQNHSoD2xbE+QkPxCAFlNEevoEH3Sl0sibVcOQVnN" crossorigin="anonymous">
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css"
          integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T" crossorigin="anonymous">
    <script src="//maxcdn.bootstrapcdn.com/bootstrap/3.3.0/js/bootstrap.min.js"></script>
    <script src="//code.jquery.com/jquery-1.11.1.min.js"></script>
    <script src="https://www.google.com/recaptcha/api.js" async defer></script>
    <script type="text/javascript">
        var onloadCallback = function () {
            grecaptcha.render('login-form', {
                'sitekey': '6LeMC5gUAAAAALDZ8NiyzD7MopL0Pt9NMsNvRFUd'
            });
        };
    </script>
    <style>
        .panel-login {
            border-color: #ccc;
            -webkit-box-shadow: 0 2px 3px 0 rgba(0, 0, 0, 0.2);
            -moz-box-shadow: 0 2px 3px 0 rgba(0, 0, 0, 0.2);
            box-shadow: 0 2px 3px 0 rgba(0, 0, 0, 0.2);
        }

        .panel-login > .panel-heading {
            color: #F55C00;
            background-color: #fff;
            border-color: grey;
            padding-bottom: 20px;
            text-align: center;
        }

        .panel-login > .panel-heading a {
            text-decoration: none;
            color: #F55C00;
            font-weight: bold;
            font-size: 15px;
            -webkit-transition: all 0.1s linear;
            -moz-transition: all 0.1s linear;
            transition: all 0.1s linear;
        }

        .panel-login > .panel-heading a.active {
            color: #F55C00;
            font-size: 18px;
        }

        .panel-login > .panel-heading hr {
            margin-top: 10px;
            margin-bottom: 0;
            clear: both;
            border: 0;
            height: 1px;
            background-image: -webkit-linear-gradient(left, rgba(0, 0, 0, 0), rgba(0, 0, 0, 0.15), rgba(0, 0, 0, 0));
            background-image: -moz-linear-gradient(left, rgba(0, 0, 0, 0), rgba(0, 0, 0, 0.15), rgba(0, 0, 0, 0));
            background-image: -ms-linear-gradient(left, rgba(0, 0, 0, 0), rgba(0, 0, 0, 0.15), rgba(0, 0, 0, 0));
            background-image: -o-linear-gradient(left, rgba(0, 0, 0, 0), rgba(0, 0, 0, 0.15), rgba(0, 0, 0, 0));
        }

        .panel-login input[type="text"], .panel-login input[type="email"], .panel-login input[type="password"] {
            height: 45px;
            border: 1px solid #ddd;
            font-size: 16px;
            -webkit-transition: all 0.1s linear;
            -moz-transition: all 0.1s linear;
            transition: all 0.1s linear;
        }

        .panel-login input:hover,
        .panel-login input:focus {
            outline: none;
            -webkit-box-shadow: none;
            -moz-box-shadow: none;
            box-shadow: none;
            border-color: #ccc;
        }

        .btn-login {
            background-color: #62CCE8;
            outline: none;
            color: white;
            font-size: 14px;
            height: auto;
            font-weight: normal;
            padding: 14px 0;
            text-transform: uppercase;
            border-color: #59B2E6;
        }

        .btn-login:hover,
        .btn-login:focus {
            color: #000000;
            background-color: #62CCE8;
            border-color: #53A3CD;
        }

        .btn-register {
            background-color: #62CCE8;
            outline: none;
            color: white;
            font-size: 14px;
            height: auto;
            font-weight: normal;
            padding: 14px 0;
            text-transform: uppercase;
            border-color: #59B2E6;
        }

        .btn-register:hover,
        .btn-register:focus {
            color: #000000;
            background-color: #62CCE8;
            border-color: #53A3CD;
        }

        .container .row .col-md-6 .panel .panel-heading .row {
            justify-content: space-around;
        }

        .container .row .col-md-6 .panel {
            padding: 0 50px;
        }

        .container .row {
            display: flex;
            justify-content: space-around;
        }

        .container {
            margin-top: 200px;
        }

        .navigation {
            background-color: #f5821f;
            position: fixed;
            left: 0;
            right: 0;
            top: 0;
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

        #popup-form {
            display: none; /* Hidden by default */
            position: fixed; /* Stay in place */
            z-index: 1; /* Sit on top */
            padding-top: 130px; /* Location of the box */
            left: 0;
            top: 0;
            width: 100%; /* Full width */
            height: 100%; /* Full height */
            overflow: auto; /* Enable scroll if needed */
            background-color: rgb(0, 0, 0); /* Fallback color */
            background-color: rgba(0, 0, 0, 0.4); /* Black w/ opacity */
        }

        .popup-content {
            background-color: #fefefe;
            margin: auto;
            padding: 20px;
            border: 1px solid #888;
            width: 35%;
        }

        .close {
            color: #aaaaaa;
            float: right;
            font-size: 28px;
            font-weight: bold;
        }

        .close:hover,
        .close:focus {
            color: #000;
            text-decoration: none;
            cursor: pointer;
        }

    </style>
</head>

<body>

<div>
    <div class="navigation">
        <span class="navigation-brand">T!ckTrack</span>
    </div>
    <div class="container">
        <div class="row">
            <div class="col-md-6 col-md-offset-3">
                <div class="panel panel-login">
                    <div class="panel-heading">
                        <div class="row">
                            <div class="col-xs-6">
                                <a href="#" class="active" id="login-form-link">Login</a>
                            </div>
                            <div class="col-xs-6">
                                <a href="#" id="register-form-link">Register</a>
                            </div>
                        </div>
                        <hr>
                    </div>
                    <div class="panel-body">
                        <div class="row">
                            <div class="col-lg-12">
                                <form id="login-form" action="/login" method="post" role="form" style="display: block;">
                                    <c:if test="${failure}">
                                        <p align="center" style="color:red">Invalid username or login</p>
                                    </c:if>
                                    <div class="form-group">
                                        <input type="text" name="username" id="username" tabindex="1"
                                               class="form-control" placeholder="Username" required="required"/>
                                    </div>
                                    <div class="form-group">
                                        <input type="password" name="password" id="password" tabindex="2"
                                               class="form-control" placeholder="Password" required="required"/>
                                    </div>
                                    <div class="form-group" align="center">
                                        <div class="g-recaptcha"
                                             data-sitekey="6LeMC5gUAAAAALDZ8NiyzD7MopL0Pt9NMsNvRFUd"></div>
                                    </div>
                                    <div class="form-group">
                                        <div class="row">
                                            <div class="col-sm-6 col-sm-offset-3">
                                                <input type="submit" name="login-submit" id="login-submit" tabindex="4"
                                                       class="form-control btn btn-login" value="Log In">
                                            </div>
                                        </div>
                                    </div>
                                    <div class="form-group">
                                        <div class="row">
                                            <div class="col-lg-12">
                                            </div>
                                        </div>
                                    </div>
                                    <c:if test="${logout}">
                                        <p align="center" style="color:red">You logged out</p>
                                    </c:if>
                                </form>
                                <form:form id="register-form" action="/register" method="post"
                                           modelAttribute="createRequest" onsubmit="return validate()" role="form"
                                           style="display: none;">
                                    <c:if test="${registerFailure}">
                                        <p id="invalidUsername" style="color:red">username already exists, please choose
                                            another one</p>
                                    </c:if>
                                    <div class="form-group">
                                        <form:input type="text" name="username" id="newUsername" tabindex="1"
                                                    class="form-control" placeholder="Username" required="required"
                                                    path="username"/>
                                    </div>
                                    <div class="form-group">
                                        <form:input type="text" name="firstname" id="firstname" tabindex="2"
                                                    class="form-control" placeholder="First Name" required="required"
                                                    path="firstname"/>
                                    </div>
                                    <div class="form-group">
                                        <form:input type="text" name="lastname" id="lastname" tabindex="2"
                                                    class="form-control" placeholder="Last Name" required="required"
                                                    path="lastname"/>
                                    </div>
                                    <label>Gender</label>
                                    <div class="form-group">
                                        <form:radiobuttons path="gender" items="${genders}" required="required"/>
                                    </div>
                                    <div class="form-group">
                                        <form:input type="email" name="email" id="email" tabindex="1"
                                                    class="form-control"
                                                    placeholder="Email Address" required="required" path="email"/>
                                    </div>
                                    <div class="form-group">
                                        <form:input type="password" name="password" id="newPassword" tabindex="2"
                                                    class="form-control" placeholder="Password" required="required"
                                                    path="password"/>
                                    </div>
                                    <div class="form-group">
                                        <input type="password" name="passwordRepeat" id="newPasswordRepeat" tabindex="3"
                                               class="form-control" placeholder="Repeat Password" required>
                                    </div>
                                    <div class="form-group" align="center">
                                        <div class="g-recaptcha"
                                             data-sitekey="6LeMC5gUAAAAALDZ8NiyzD7MopL0Pt9NMsNvRFUd"></div>
                                    </div>
                                    <p id="error" style="color:red"></p>
                                    <div class="form-group">
                                        <div class="row">
                                            <div class="col-sm-6 col-sm-offset-3">
                                                <input type="submit" name="register-submit" id="register-submit"
                                                       tabindex="4" class="form-control btn btn-register"
                                                       value="Register Now">
                                            </div>
                                        </div>
                                    </div>
                                </form:form>
                                <div id="popup-form">
                                    <form id="changePassword" class="popup-content" method="get"
                                          action="/fromLogin/getChangePasswordLink">
                                        <span class="close">&times;</span>
                                        <div class="form-group">
                                            <input type="text" name="username" class="form-control"
                                                   placeholder="Your username" required>
                                        </div>
                                        <div class="form-group">
                                            <input type="submit" class="btn btn-info" name="submit" value="Get link"/>
                                        </div>
                                    </form>
                                </div>
                                <div align="center">
                                    <a id="opener" name="forgotPassword" href="#">Forgot password?</a>
                                    <p style="color:blue">${forgotPasswordResponse}</p>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<script src="https://www.google.com/recaptcha/api.js?onload=onloadCallback&render=explicit"
        async defer>
</script>
</body>
</html>

<script>
    $(function () {

        $('#login-form-link').click(function (e) {
            $("#login-form").delay(100).fadeIn(100);
            $("#register-form").fadeOut(100);
            $('#register-form-link').removeClass('active');
            $(this).addClass('active');
            e.preventDefault();
        });
        $('#register-form-link').click(function (e) {
            $("#register-form").delay(100).fadeIn(100);
            $("#login-form").fadeOut(100);
            $('#login-form-link').removeClass('active');
            $(this).addClass('active');
            e.preventDefault();
        });

    });

    function validate() {
        var password = document.getElementById("newPassword").value;
        var passwordRepeat = document.getElementById("newPasswordRepeat").value;
        if (password !== passwordRepeat) {
            document.getElementById("error").innerHTML = "Password value does not match";
            return false;
        }
    }

    var modal = document.getElementById("popup-form");
    var btn = document.getElementById("opener");

    var span = document.getElementsByClassName("close")[0];

    btn.onclick = function () {

        modal.style.display = "block";
    };
    span.onclick = function () {

        modal.style.display = "none";

    };

    window.onclick = function (event) {
        if (event.target === modal) {
            modal.style.display = "none";
        }
    }
</script>