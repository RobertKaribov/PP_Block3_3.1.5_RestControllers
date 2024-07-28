document.addEventListener('DOMContentLoaded', function () {
    // Находим все панели ролей
    const rolePanels = document.querySelectorAll('.rolePanel');
    // Находим все ссылки ролей
    const roleLinks = document.querySelectorAll('.roleLink');

    // Функция для скрытия всех панелей ролей
    function hideAllRolePanels() {
        // Проходим по всем панелям и скрываем их
        rolePanels.forEach(panel => panel.style.display = 'none');
    }

    // Функция для сброса стиля активной роли
    function resetActiveRoleStyle() {
        // Находим текущую активную роль
        const activeRole = document.querySelector('.bg-primary');
        // Если активная роль найдена, убираем классы стилей
        if (activeRole) {
            activeRole.classList.remove('bg-primary', 'text-white');
        }
    }

    // Для каждой ссылки ролей
    roleLinks.forEach(link => {
        // Добавляем обработчик события клика
        link.addEventListener('click', event => {
            // Предотвращаем стандартное поведение ссылки
            event.preventDefault();

            // Сбрасываем стиль активной роли
            resetActiveRoleStyle();
            // Скрываем все панели ролей
            hideAllRolePanels();

            // Добавляем классы стилей кликнутой ссылке
            event.target.classList.add('bg-primary', 'text-white');
            // Получаем панель роли по data-role атрибуту
            const rolePanel =
                document.getElementById(
                    `${event.target
                        .getAttribute('data-role')
                        .toLowerCase()}Panel`);
            // Если панель роли найдена, показываем её
            if (rolePanel) {
                rolePanel.style.display = 'block';
            }
        });
    });

    // Скрываем все панели ролей при загрузке страницы
    hideAllRolePanels();
    // Делаем панель администратора видимой при загрузке страницы
    document.getElementById('adminPanel').style.display = 'block';

    // код выше реализует смену ролей в админ панели в левой части страницы
    // Функция для заполнения формы данными пользователя
    function fillFormWithData(button,
                              userIdInputId,
                              firstNameInputId,
                              lastNameInputId,
                              ageInputId) {
        let id = button.data('id');
        let firstname = button.data('firstname');
        let lastname = button.data('lastname');
        let age = button.data('age');

        // Заполняем поля формы полученными данными
        $(userIdInputId).val(id);
        $(firstNameInputId).val(firstname);
        $(lastNameInputId).val(lastname);
        $(ageInputId).val(age);
    }

    let originalUserRoles = null;
    // для отображения данных в модальном окне редактирования пользователя
    // Обработчик клика по кнопке редактирования пользователя
    $('.edit-user-button').click(function () {
        // Заполняем форму данными пользователя
        fillFormWithData($(this),
            '#editUserId',
            '#editFirstName',
            '#editLastName',
            '#editAge');
        // запоминаем ранее выбранные роли пользователя
        originalUserRoles = $(this).data('role');
    });

    // Обработчик клика по кнопке удаления пользователя
    $('.delete-user-button').click(function () {
        // Заполняем форму данными пользователя
        fillFormWithData($(this),
            '#deleteUserId',
            '#deleteUserFirstName',
            '#deleteUserLastName',
            '#deleteUserAge');
        // Устанавливаем роль пользователя в форму
        $('#deleteUserRole').val($(this).data('role'));
    });

    // Обработчик клика по кнопке сохранения изменений в модальном окне редактирования
    document.querySelector('#editUserModal .save-changes').addEventListener('click', function (e) {
        e.preventDefault();

        // Получаем данные из формы
        let id = document.querySelector('#editUserId').value;
        let firstname = document.querySelector('#editFirstName').value;
        let lastname = document.querySelector('#editLastName').value;
        let age = document.querySelector('#editAge').value;
        let password = document.querySelector('#editPassword').value;

        // Получаем токен CSRF и заголовок из мета-тегов
        let token = $("meta[name='_csrf']").attr("content");
        let header = $("meta[name='_csrf_header']").attr("content");

        // Получаем выбранные роли из выпадающего списка
        let selectElement = document.querySelector('#editRole');
        let selectedOptions = Array.from(selectElement.selectedOptions);
        let roles = selectedOptions.map(option => {
            if (option.value === "User") {
                return "ROLE_USER";
            } else if (option.value === "Admin") {
                return "ROLE_ADMIN";
            }
        });

        // Формируем параметры запроса
        let params = new URLSearchParams({
            id: id,
            firstname: firstname,
            lastname: lastname,
            age: age,
            password: password,
        });

        // Добавляем роли в параметры запроса
        roles.forEach(role => params.append('role', role)); // Добавляем каждую роль

        // Отправляем запрос на обновление пользователя
        fetch('/admin/updateUser', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
                [header]: token
            },
            body: params,
        })
            // После успешного запроса скрываем модальное окно и обновляем страницу
            .then(response => {
                $('#editUserModal').modal('hide');
                location.reload();
            });
    });

    // обработка кнопки Delete в модальном окне удаления пользователя.
    document.querySelector('.delete-user-button-confirm').addEventListener('click', function (e) {
        // Предотвращаем стандартное поведение кнопки
        e.preventDefault();

        // Получаем id пользователя из формы
        let id = document.querySelector('#deleteUserId').value;

        // Получаем токен CSRF и заголовок из мета-тегов
        let token = $("meta[name='_csrf']").attr("content");
        let header = $("meta[name='_csrf_header']").attr("content");

        // Отправляем запрос на удаление пользователя
        fetch('/admin/deleteUser', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
                [header]: token
            },
            body: new URLSearchParams({
                id: id
            }),
        })
            // После успешного запроса скрываем модальное окно и обновляем страницу
            .then(response => {
                $('#deleteUserModal').modal('hide');
                location.reload();
            })
    });

    // Обработчик отправки формы добавления нового пользователя
    document.querySelector('.add-user-form').addEventListener('submit', function (event) {
        // Предотвращаем стандартное поведение формы
        event.preventDefault();

        // Получаем данные из формы
        let firstname = document.querySelector('#new_firstName').value;
        let lastname = document.querySelector('#new_lastName').value;
        let age = document.querySelector('#new_age').value;
        let password = document.querySelector('#new_password').value;

        // Получаем токен CSRF и заголовок из мета-тегов
        let token = $("meta[name='_csrf']").attr("content");
        let header = $("meta[name='_csrf_header']").attr("content");

        // Получаем выбранные роли из выпадающего списка
        let selectElement = document.querySelector('#new_role');
        let selectedOptions = Array.from(selectElement.selectedOptions);

        // Преобразуем роли в формат для отправки на сервер
        let roles = selectedOptions.map(option => {
            if (option.value === "User") {
                return "ROLE_USER";
            } else if (option.value === "Admin") {
                return "ROLE_ADMIN";
            }
        });

        // Формируем параметры запроса
        let params = new URLSearchParams({
            firstname: firstname,
            lastname: lastname,
            age: age,
            password: password,
        });

        // Добавляем роли в параметры запроса
        roles.forEach(role => params.append('role', role)); // Добавляем каждую роль

        // Отправляем запрос на добавление пользователя
        fetch('/admin/addUser', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
                [header]: token
            },
            body: params,
        })
            // После успешного запроса обновляем страницу
            .then(response => {
                location.reload();
            });
    });

});

