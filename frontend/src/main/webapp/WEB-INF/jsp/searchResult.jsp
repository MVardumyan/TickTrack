<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Bootstrap Example</title>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.0/css/bootstrap.min.css">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.0/js/bootstrap.min.js"></script>
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
    </style>
</head>

<body>

<div class="container">
    <table id="dtBasicExample" class="table table-striped table-bordered table-sm" cellspacing="0" width="100%">
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
        $('#dtBasicExample').DataTable();
        $('.dataTables_length').addClass('bs-select');
    });
</script>