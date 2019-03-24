<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
<style>

    body {
        background-image: url("data:image/svg+xml;utf8,%3Csvg version='1.1' xmlns='http://www.w3.org/2000/svg' xmlns:xlink='http://www.w3.org/1999/xlink' width='5' height='5' style='background%2Dcolor:hsl(31,100%,56%);'%3E%3Cline x2='5' y2='5' stroke='hsl(40,100%,68%)' /%3E%3C/svg%3E");
    }

    svg {
        width: 150px;
        overflow: visible;
        pointer-events:none;
    }

    .a {
        display: grid;
        grid-template-columns: 217px;
        grid-template-rows: 115px;
    }

    .b {
        display: grid;
        width: 415px;
        height: 265px;
        grid-template-columns: repeat(3, 132px);
        grid-template-rows: 115px;

        position: absolute;
        margin: auto;
        top: 0;
        bottom: 0;
        left: 0;
        right: 0;
    }

    svg {
        display: block;
        justify-self: start;
    }

    svg:nth-of-type(2) {
        justify-self: end;
    }

    /*SVG*/

    #root {
        position: absolute;
        width: 1px;
        left: -100em;
    }

    svg * {
    }
    .theHex {
        pointer-events: all;
    }
    #theHex {
        stroke: #522814;
        stroke-width: 5px;
    }
    use {
        transition: transform 0.3s ease-out;
    }
    .use_lb {
        transform-origin: 47px 101px;
    }
    .use_lt {
        transform-origin: 44px 96px;
    }
    .use_rb {
        transform-origin: 105px 101px;
    }
    .use_rt {
        transform-origin: 108px 96px;
    }
    .use_eyes {
        transform-origin: 104px 76px;
    }

    .theHex ~ use {
        pointer-events: none;
    }

    .theHex:hover ~ .use_lb,
    .theHex:hover ~ .use_lt {
        transform: rotate(25deg);
    }

    .theHex:hover ~ .use_rb,
    .theHex:hover ~ .use_rt {
        transform: rotate(-25deg);
    }

    .theHex:hover ~ .use_eyes {
        transform: scaleY(0.5);
    }

    #snout,
    #ears,
    #wiskers,
    #eyes {
        fill: #522814;
    }

    .tongue {
        fill: hsla(0,100%,50%,.5);
    }


    .sidnav {
        height: 200%;
        position: absolute;
        right: 86%;
        left: 0;
        top: 34px;
        background-color: #474c55;
    }

    .navigation {
        background-color: brown;
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

    section span{
        display: block;
    }

    .title-one{
        color: #fff;
        font-size: 60px;
        font-weight: 700;
        letter-spacing: 8px;
        margin-bottom: 100px;
        /*background: #3a3a3a;*/
        position: relative;
        animation: text 3s 1;
    }

    .title-two{
        font-size: 50px;
        color: brown;
        animation: text2 3s 1;
        margin-bottom: 100px;
    }

    .title-section {
        margin-left: 165px;
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



</style>

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
        <a href="/myTickets/1/30" class="sidnav-item">MY TICKETS</a>
    </div>

    <div class="sidnav-item-conteiner">
        <a href="/newTicket" class="sidnav-item">NEW TICKET</a>
    </div>

    <div class="sidnav-item-conteiner">
        <a href="/admin/userManagement/1/30" class="sidnav-item">USER MANAGEMENT</a>
    </div>

    <div class="sidnav-item-conteiner">
        <a href="/admin/categoryManagement" class="sidnav-item">CATEGORY MANAGEMENT</a>
    </div>

    <div class="sidnav-item-conteiner">
        <a href="/admin/groupManagement" class="sidnav-item">GROUP MANAGEMENT</a>
    </div>
</div>
<div class="b">

    <div class="a">


        <div class="a">
            <svg viewBox="0 0 150 150" class="used">
                <use xlink:href="#theHex" class="theHex" fill="#ffca5e" />

                <use xlink:href="#_lb_wisker" class="use_lb"  width="110" x="20" y="15" />
                <use xlink:href="#_lt_wisker" class="use_lt"  width="110" x="20" y="15" />
                <use xlink:href="#_rb_wisker" class="use_rb"  width="110" x="20" y="15" />
                <use xlink:href="#_rt_wisker" class="use_rt"  width="110" x="20" y="15" />
                <use xlink:href="#eyes"  class="use_eyes"  width="110" x="20" y="15" />

                <path class="tongue" d="M75 97H65A5,10 0 0 0 85,97z" />
                <use xlink:href="#thecat"  width="110" x="20" y="15" />

            </svg>
        </div>
    </div>

    <svg viewBox="0 0 150 150" width="1" id="root">
        <defs>

            <symbol id="thecat" viewBox="0 0 120 120" >
                <path id="snout"  d="M60.355,68.971c-6.096,4.684-10.809,5.317-14.413,1.48c-1.275-1.358-2.395-4.752-1.638-5.77
	c2.121-2.854,3.688-0.457,5.209,1.514c2.111,2.736,4.582,1.498,6.422-0.305c1.678-1.644,3.942-3.74,0.751-6.146
	c-2.091-1.576-3.138-4.14-0.671-5.119c2.697-1.071,5.708-1.523,9.052,0.185c1.635,1.414,0.174,3.384-0.792,4.443
	c-2.604,2.858-1.254,5.079,0.871,6.749c2.289,1.797,4.898,2.686,7.205-0.719c0.805-1.188,3.344-2.668,4.506-2.141
	c1.494,1.119,0.793,3.934,0.064,5.301C73.988,73.949,67.191,74.123,60.355,68.971z"/>

                <g id="ears">
                    <path id="l_ear" d="M0.549,22.902C0.359,11.312,5.463,4.237,16.305,1.164c1.019-0.288,2.954-0.062,3.52,0.778
	c0.969,2.364-1.785,4.089-2.219,4.324C9.243,10.795,5.687,17.926,6.16,27.271c-0.148,3.132-1.513,5.434-3.065,5.434
	c-1.519,0-2.132-2.443-2.44-3.834C0.231,26.969,0.549,24.9,0.549,22.902z"/>
                    <path id="r_ear" d="M119.779,28.871c-0.309,1.391-0.922,3.834-2.441,3.834c-1.551,0-2.916-2.302-3.064-5.434
	c0.473-9.345-3.083-16.476-11.445-21.005c-0.435-0.235-3.188-1.96-2.22-4.324c0.566-0.84,2.501-1.066,3.521-0.778
	c10.842,3.073,15.945,10.148,15.756,21.738C119.885,24.9,120.203,26.969,119.779,28.871z"/>
                </g>
            </symbol>


            <g id="wiskers">
                <symbol id="_lb_wisker" viewBox="0 0 120 120" >
                    <path id="lb_wisker" d="M28.962,72.202c0.875,2.007-13.469,10.183-14.186,8.491C12.966,77.405,28.079,70.452,28.962,72.202z" />
                </symbol>
                <symbol id="_lt_wisker" viewBox="0 0 120 120" >
                    <path  id="lt_wisker"  d="M25.74,66.219c0.455,1.896-14.707,7.321-15.318,4.727C9.845,68.933,24.709,63.314,25.74,66.219z"/>
                </symbol>
                <symbol id="_rb_wisker" viewBox="0 0 120 120" >
                    <path id="rb_wisker" d="M106.629,80.693c-0.716,1.691-15.061-6.484-14.186-8.491C93.327,70.452,108.439,77.405,106.629,80.693z"/>
                </symbol>
                <symbol id="_rt_wisker" viewBox="0 0 120 120" >
                    <path id="rt_wisker"  d="M110.984,70.945c-0.611,2.595-15.773-2.83-15.318-4.727C96.697,63.314,111.562,68.933,110.984,70.945z"/>
                </symbol>
            </g>

            <symbol id="eyes" viewBox="0 0 120 120"  >
                <circle id="R_eye" r="7" cx="91" cy="45" />
                <use xlink:href="#R_eye"  transform="translate(-60.5,0)" />
            </symbol>
            <g id="theHex">
                <!--<use xlink:href="#hx" stroke="#432010" stroke-width="20" />-->
                <path id="hx" d="M66.34,10
                   Q75,5 83.66,10
                   L126.96,35
                   Q135.62,40 135.62,50
                   L135.62,100
                   Q135.62,110 126.96,115
                   L83.66,140
                   Q75,145 66.34,140
                   L23,115 Q14.38,110 14.38,100
                   L14.38,50
                   Q14.38,40 23,35Z"></path>

            </g>
        </defs>
    </svg>
</div>
<section class="title-section">
    <span class="title-one">Hello!</span>
    <span class="title-two">${name}</span>
</section>
</body>
</html>