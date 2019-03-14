<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Admin: Category Management</title>
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
            background-color: brown;
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
            color: #f5821f;
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

        #popup-form, #update-popup-form {
            display: none; /* Hidden by default */
            position: fixed; /* Stay in place */
            z-index: 1; /* Sit on top */
            padding-top: 100px; /* Location of the box */
            left: 0;
            top: 0;
            width: 100%; /* Full width */
            height: 100%; /* Full height */
            overflow: auto; /* Enable scroll if needed */
            background-color: rgb(0,0,0); /* Fallback color */
            background-color: rgba(0,0,0,0.4); /* Black w/ opacity */
        }

        .popup-content {
            background-color: #fefefe;
            margin: auto;
            padding: 20px;
            border: 1px solid #888;
            width: 80%;
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

    <div class="sidnav-item-conteiner">
        <a href="/admin/userManagement" class="sidnav-item">USER MANAGEMENT</a>
    </div>

    <div class="sidnav-item-conteiner">
        <a href="/admin/categoryManagement" class="sidnav-item">CATEGORY MANAGEMENT</a>
    </div>

    <div class="sidnav-item-conteiner">
        <a href="/admin/groupManagement" class="sidnav-item">GROUP MANAGEMENT</a>
    </div>
</div>

<div class="body" align="left">
    <button id="opener" class="btn btn-info" name="createCategory" style="margin-bottom:30px;margin-top:20px;">Create new Category</button>
    <div id="popup-form">
        <form id="createCategory" class="popup-content" method="post" action="/admin/createCategory">
            <span class="close">&times;</span>
            <div class="form-group">
                <input type="text" name="newCategory" class="form-control" placeholder="Category name" required>
            </div>
            <div class="form-group">
                <input type="submit" class="btn btn-info" name="submit" value="Create"/>
            </div>
        </form>
    </div>
    <button id="updateOpener" class="btn btn-info" name="updateCategory" style="margin-bottom:30px;margin-top:20px;">Change name</button>
    <div id="update-popup-form">
        <form id="updateCategory" class="popup-content" method="post" action="/admin/updateCategory">
            <span class="close">&times;</span>
            <div class="form-group">
                <input type="text" name="oldName" class="form-control" placeholder="Old name" required>
            </div>
            <div class="form-group">
                <input type="text" name="newName" class="form-control" placeholder="New name" required>
            </div>
            <div class="form-group">
                <input type="submit" class="btn btn-info" name="submit" value="Update"/>
            </div>
        </form>
    </div>

    <h4>Categories</h4>
    <table id="categoriesTable" class="table table-striped table-bordered table-sm" width="100%">
        <thead>
        <tr>
            <th class="th-sm">Category
            </th>
            <th class="th-sm">Deactivate
            </th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${categories}" var="category">
            <tr>
                <td>${category.categoryName}</td>
                <c:choose>
                    <c:when test="${category.isDeactivated}">
                        <td>Deactivated</td>
                    </c:when>
                    <c:otherwise>
                        <td>
                            <button onclick="location.href='/admin/deactivateCategory/${category.categoryName}'"
                                    type="button" class="deactivate btn btn-info">Deactivate
                            </button>
                        </td>
                    </c:otherwise>
                </c:choose>
            </tr>
        </c:forEach>
        </tbody>
        <tfoot>
        <tr>
            <th>Category
            </th>
            <th>Deactivate
            </th>
        </tr>
        </tfoot>
    </table>
</div>

<script>
    $(document).ready(function () {
        $('#regularUserTable,#businessUserTable,#adminTable').DataTable();
        $('.dataTables_length').addClass('bs-select');
    });

    var modal = document.getElementById("popup-form");
    var btn = document.getElementById("opener");

    var updateModal = document.getElementById("update-popup-form");
    var updateBtn = document.getElementById("updateOpener");

    var span = document.getElementsByClassName("close")[0];
    var updateSpan = document.getElementsByClassName("close")[1];

    btn.onclick = function () {

        modal.style.display = "block";
    };
    span.onclick = function () {

        modal.style.display = "none";

    };

    updateBtn.onclick = function () {
        updateModal.style.display = "block";
    };

    updateSpan.onclick = function () {
        updateModal.style.display = "none";
    };

    window.onclick = function (event) {
        if (event.target === updateModal) {
            updateModal.style.display = "none";
        }
        if (event.target === modal) {
            modal.style.display = "none";
        }
    }
</script>

</body>
</html>