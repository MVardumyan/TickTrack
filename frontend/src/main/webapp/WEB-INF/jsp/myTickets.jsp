<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Ticket Search Result</title>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/4.1.3/css/bootstrap.css">
    <link rel="stylesheet" href="https://cdn.datatables.net/1.10.19/css/dataTables.bootstrap4.min.css">
    <link href="//maxcdn.bootstrapcdn.com/bootstrap/3.3.0/css/bootstrap.min.css" rel="stylesheet" id="bootstrap-css">

    <script src="https://code.jquery.com/jquery-3.3.1.js"></script>
    <script src="https://cdn.datatables.net/1.10.19/js/jquery.dataTables.min.js"></script>
    <script src="https://cdn.datatables.net/1.10.19/js/dataTables.bootstrap4.min.js"></script>
    <style>
        table.dataTable thead .sorting:after,
        table.dataTable thead .sorting:before,
        table.dataTable thead .sorting_asc:after,
        table.dataTable thead .sorting_asc:before,
        table.dataTable thead .sorting_asc_disabled:after,
        table.dataTable thead .sorting_asc_disabled:before,
        table.dataTable thead .sorting_desc:after,
        table.dataTable thead .sorting_desc:before,
        table.dataTable thead .sorting_desc_disabled:after,
        table.dataTable thead .sorting_desc_disabled:before {
            bottom: .5em;
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
            padding-right: 50px;
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

        .searchResult_wrapper {
            margin-bottom: 70px;
        }
    </style>
</head>

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
        <a href="http://localhost:9093//myTickets" class="sidnav-item">MY TICKETS</a>
    </div>

    <div class="sidnav-item-conteiner">
        <a href="http://localhost:9093/newTicket" class="sidnav-item">NEW TICKET</a>
    </div>
</div>
<div class="body" align="left">
    <h4>Tickets created by me</h4>
    <table id="searchResult" class="table table-striped table-bordered table-sm" width="100%">
        <thead>
        <tr>
            <th class="th-sm">ID
            </th>
            <th class="th-sm">Summary
            </th>
            <th class="th-sm">Assignee
            </th>
            <th class="th-sm">Creator
            </th>
            <th class="th-sm">Category
            </th>
            <th class="th-sm">Status
            </th>
            <th class="th-sm">Priority
            </th>
            <th class="th-sm">Open date
            </th>
            <th class="th-sm">Close date
            </th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${tickets}" var="ticket">
            <tr>
                <td>${ticket.ticketID}</td>
                <td>${ticket.summary}</td>
                <td>${ticket.assignee}</td>
                <td>${ticket.creator}</td>
                <td>${ticket.category}</td>
                <td>${ticket.status}</td>
                <td>${ticket.priority}</td>
                <td>${ticket.openDate}</td>
                <td>${ticket.closeDate}</td>
            </tr>
        </c:forEach>
        </tbody>
        <tfoot>
        <tr>
            <th>ID
            </th>
            <th>Summary
            </th>
            <th>Assignee
            </th>
            <th>Creator
            </th>
            <th>Category
            </th>
            <th>Status
            </th>
            <th>Priority
            </th>
            <th>Open date
            </th>
            <th>Close date
            </th>
        </tr>
        </tfoot>
    </table>




    <h4>Tickets assigned to me</h4>
    <table id="tickets-assigned-to-me" class="table table-striped table-bordered table-sm" width="100%">
        <thead>
        <tr>
            <th class="th-sm">ID
            </th>
            <th class="th-sm">Summary
            </th>
            <th class="th-sm">Assignee
            </th>
            <th class="th-sm">Creator
            </th>
            <th class="th-sm">Category
            </th>
            <th class="th-sm">Status
            </th>
            <th class="th-sm">Priority
            </th>
            <th class="th-sm">Open date
            </th>
            <th class="th-sm">Close date
            </th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${tickets}" var="ticket">
            <tr>
                <td>${ticket.ticketID}</td>
                <td>${ticket.summary}</td>
                <td>${ticket.assignee}</td>
                <td>${ticket.creator}</td>
                <td>${ticket.category}</td>
                <td>${ticket.status}</td>
                <td>${ticket.priority}</td>
                <td>${ticket.openDate}</td>
                <td>${ticket.closeDate}</td>
            </tr>
        </c:forEach>
        </tbody>
        <tfoot>
        <tr>
            <th>ID
            </th>
            <th>Summary
            </th>
            <th>Assignee
            </th>
            <th>Creator
            </th>
            <th>Category
            </th>
            <th>Status
            </th>
            <th>Priority
            </th>
            <th>Open date
            </th>
            <th>Close date
            </th>
        </tr>
        </tfoot>
    </table>



    <h4>My groups tickets</h4>
    <table id="my-groups-tickets" class="table table-striped table-bordered table-sm" width="100%">
        <thead>
        <tr>
            <th class="th-sm">ID
            </th>
            <th class="th-sm">Summary
            </th>
            <th class="th-sm">Assignee
            </th>
            <th class="th-sm">Creator
            </th>
            <th class="th-sm">Category
            </th>
            <th class="th-sm">Status
            </th>
            <th class="th-sm">Priority
            </th>
            <th class="th-sm">Open date
            </th>
            <th class="th-sm">Close date
            </th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${tickets}" var="ticket">
            <tr>
                <td>${ticket.ticketID}</td>
                <td>${ticket.summary}</td>
                <td>${ticket.assignee}</td>
                <td>${ticket.creator}</td>
                <td>${ticket.category}</td>
                <td>${ticket.status}</td>
                <td>${ticket.priority}</td>
                <td>${ticket.openDate}</td>
                <td>${ticket.closeDate}</td>
            </tr>
        </c:forEach>
        </tbody>
        <tfoot>
        <tr>
            <th>ID
            </th>
            <th>Summary
            </th>
            <th>Assignee
            </th>
            <th>Creator
            </th>
            <th>Category
            </th>
            <th>Status
            </th>
            <th>Priority
            </th>
            <th>Open date
            </th>
            <th>Close date
            </th>
        </tr>
        </tfoot>
    </table>
</div>

</body>
</html>

<script>
    $(document).ready(function () {
        $('#searchResult').DataTable();
        $('.dataTables_length').addClass('bs-select');
    });

    $(document).ready(function () {
        $('#tickets-assigned-to-me').DataTable();
        $('.dataTables_length').addClass('bs-select');
    });

    $(document).ready(function () {
        $('#my-groups-tickets').DataTable();
        $('.dataTables_length').addClass('bs-select');
    });
    function goBack() {
        window.history.back();
    }
</script>