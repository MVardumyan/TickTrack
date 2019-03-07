<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Ticket Search Result</title>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/4.1.3/css/bootstrap.css">
    <link rel="stylesheet" href="https://cdn.datatables.net/1.10.19/css/dataTables.bootstrap4.min.css">
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
        .glyphicon-search,
        .glyphicon-user {
            margin-right: 20px;
        }
    </style>
</head>

<body>
<div class="navigation">
    <span class="navigation-brand">T!ckTrack</span>

    <div class="navigation-action-button">
        <span class="glyphicon glyphicon-search"></span>
        <span class="glyphicon glyphicon-user"></span>
        <span class="glyphicon glyphicon-log-out"></span>
    </div>
</div>
<br/><br/>
<div class="container" align="left">
    <button onclick="goBack()" class="btn btn-info" name="back" style="margin-bottom:20px;margin-top:20px">Back to search form</button>
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
</div>

</body>
</html>

<script>
    $(document).ready(function () {
        $('#searchResult').DataTable();
        $('.dataTables_length').addClass('bs-select');
    });
    function goBack() {
        window.history.back();
    }
</script>