<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Change Password</title>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
    <link href="https://stackpath.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css" rel="stylesheet"
          integrity="sha384-wvfXpqpZZVQGK6TAh5PVlGOfQNHSoD2xbE+QkPxCAFlNEevoEH3Sl0sibVcOQVnN" crossorigin="anonymous">
    <script src="//maxcdn.bootstrapcdn.com/bootstrap/3.3.0/js/bootstrap.min.js"></script>

    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.1.0/jquery.min.js"></script>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css"/>
    <script
            src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
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

        .container {
            padding-left: 190px;
            padding-top: 40px;
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

    </style>
</head>

<body>
<div class="navigation">
    <span class="navigation-brand">T!ckTrack</span>
</div>

<div class="container" align="left">
    <h4 align="left" class="container__header">Edit User Information</h4>

    <form method="post" action="/fromLogin/changePassword/${link}" id="create_form">
        <label>Username</label>
        <div class="form-group">
            <input type="text" class="form-control" name="username"
                   placeholder="username">
        </div>
        <label>New Password</label>
        <div class="form-group">
            <input type="password" class="form-control" name="newPassword"
                   placeholder="new Password">
        </div>
        <div class="form-group">
            <input type="submit" class="btn btn-info" name="submit" value="Done"/>
        </div>
    </form>
    <br/>
</div>

</body>
</html>