<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<link href="//maxcdn.bootstrapcdn.com/bootstrap/3.3.0/css/bootstrap.min.css" rel="stylesheet" id="bootstrap-css">

<style>
    body {
        margin: 0;
    }

    .body {
        width: 100%;
        height: 100%;
        background-color: #f5821f;
    }

    *{
        padding: 0;
        margin: 0;
        font-family: sans-serif;
    }

    body {
        background: #3a3a3a;
    }

    section {
        text-align: center;
        position: absolute;
        top: 50%;
        left: 50%;
        transform: translate(-50%, -50%);
    }

    section span{
        display: block;
    }

    .title-one{
        color: #fff;
        font-size: 60px;
        font-weight: 700;
        letter-spacing: 8px;
        margin-bottom: 20px;
        /*background: #3a3a3a;*/
        position: relative;
        animation: text 3s 1;
    }

    .title-two{
        font-size: 30px;
        color: brown;
        animation: text2 3s 1;
    }

    @keyframes text {
        0%{
            opacity: 0;
            margin-bottom: -40px;
        }
        30%{
            letter-spacing: 25px;
            margin-bottom: -40px;
        }
        85%{
            letter-spacing: 8px;
            margin-bottom: -40px;
        }
    }

    @keyframes text2 {
        0%{
            opacity: 0;
        }
        30%{
            opacity: 0;
        }
        85%{
            opacity: 0;
        }
        100%{
            opacity: 1;
        }
    }

    .login-button {
        float: right;
        border: none;
        border-radius: 3px;
        width: 100px;
        height: 30px;
        margin: 10px;
        background-color: brown;
        color: wheat;
    }

</style>

<div class="body">
    <form action="/login">
        <button class="login-button">Log In</button>
    </form>

    <section>
        <span class="title-one">T!ckTrack</span>
        <span class="title-two">Welcome!</span>
    </section>
</div>
</html>