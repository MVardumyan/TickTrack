<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Admin: User Management</title>
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

<div class="body" align="left">
    <h4>Regular Users</h4>
    <table id="regularUserTable" class="table table-striped table-bordered table-sm" width="100%">
        <thead>
        <tr>
            <th class="th-sm">Username
            </th>
            <th class="th-sm">First name
            </th>
            <th class="th-sm">Last name
            </th>
            <th class="th-sm">Email address
            </th>
            <th class="th-sm">Gender
            </th>
            <th class="th-sm">Edit user
            </th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${regularUserInfo}" var="regularUser">
            <tr>
                <td><a href="/personalInfo/${regularUser.username}">${regularUser.username}</a></td>
                <td>${regularUser.firstname}</td>
                <td>${regularUser.lastname}</td>
                <td>${regularUser.email}</td>
                <td>${regularUser.gender}</td>
                <td>
                    <button onclick="location.href='/updateUserInfo/${regularUser.username}'" type="button" class="btn btn-info">edit</button>
                    <c:choose>
                    <c:when test="${regularUser.isActive}">
                <td>
                    <button onclick="location.href='/deactivate/${regularUser.username}'" type="button" class="btn btn-info">deactivate</button>
                </td>
                </c:when>
                <c:otherwise>
                <td>Deactivated</td>
                </c:otherwise>
                </c:choose>
                </td>
            </tr>
        </c:forEach>
        </tbody>
        <tfoot>
        <tr>
            <th>Username
            </th>
            <th>First name
            </th>
            <th>Last name
            </th>
            <th>Email address
            </th>
            <th>Gender
            </th>
            <th>Edit user
            </th>
        </tr>
        </tfoot>
    </table>

    <h4>Business Users</h4>
    <table id="businessUserTable" class="table table-striped table-bordered table-sm" width="100%">
        <thead>
        <tr>
            <th class="th-sm">Username
            </th>
            <th class="th-sm">First name
            </th>
            <th class="th-sm">Last name
            </th>
            <th class="th-sm">Email address
            </th>
            <th class="th-sm">Gender
            </th>
            <th class="th-sm">Edit user
            </th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${businessUserInfo}" var="businessUser">
            <tr>
                <td><a href="/personalInfo/${businessUser.username}">${businessUser.username}</a></td>
                <td>${businessUser.firstname}</td>
                <td>${businessUser.lastname}</td>
                <td>${businessUser.email}</td>
                <td>${businessUser.gender}</td>
                <td>
                    <button onclick="location.href='/updateUserInfo/${businessUser.username}'" type="button" class="btn btn-info">edit</button>
                    <c:choose>
                    <c:when test="${businessUser.isActive}">
                <td>
                    <button onclick="location.href='/deactivate/${businessUser.username}'" type="button" class="btn btn-info">deactivate</button>
                </td>

                </c:when>
                <c:otherwise>
                    <td>Deactivated</td>
                </c:otherwise>
                </c:choose>

                </td>
            </tr>
        </c:forEach>
        </tbody>
        <tfoot>
        <tr>
            <th>Username
            </th>
            <th>First name
            </th>
            <th>Last name
            </th>
            <th>Email address
            </th>
            <th>Gender
            </th>
            <th>Edit user
            </th>
        </tr>
        </tfoot>
    </table>

    <h4>Administrators</h4>
    <table id="adminTable" class="table table-striped table-bordered table-sm" width="100%">
        <thead>
        <tr>
            <th class="th-sm">Username
            </th>
            <th class="th-sm">First name
            </th>
            <th class="th-sm">Last name
            </th>
            <th class="th-sm">Email address
            </th>
            <th class="th-sm">Gender
            </th>
            <th class="th-sm">Edit user
            </th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${adminInfo}" var="adminUser">
            <tr>
                <td><a href="/personalInfo/${adminUser.username}">${adminUser.username}</a></td>
                <td>${adminUser.firstname}</td>
                <td>${adminUser.lastname}</td>
                <td>${adminUser.email}</td>
                <td>${adminUser.gender}</td>
                <td>
                    <button onclick="location.href='/updateUserInfo/${adminUser.username}'" type="button" class="btn btn-info">edit</button>
                </td>
            </tr>
        </c:forEach>
        </tbody>
        <tfoot>
        <tr>
            <th>Username
            </th>
            <th>First name
            </th>
            <th>Last name
            </th>
            <th>Email address
            </th>
            <th>Gender
            </th>
            <th>Edit user
            </th>
        </tr>
        </tfoot>
    </table>
</div>
</body>
</html>

<script>
    $(document).ready(function () {
        $('#regularUserTable,#businessUserTable,#adminTable').DataTable();
        $('.dataTables_length').addClass('bs-select');
    });
</script>