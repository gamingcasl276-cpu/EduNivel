
const $ = (selector, root = document) => root.querySelector(selector);
const $$ = (selector, root = document) => [...root.querySelectorAll(selector)];

/*
 * EduNivel 2.0
 * Navegación limpia, sin preguntas ni actividades demostrativas.
 * Los exámenes reales se gestionan desde el módulo de Spring Boot.
 */

// Secciones que conservaremos
const pageNames = {
    inicio: "Inicio",
    nosotros: "Quiénes somos",
    evaluaciones: "Evaluaciones",
    progreso: "Mi progreso"
};

// Secciones demostrativas que retiraremos
const paginasRetiradas = [
    "repaso",
    "juegos",
    "carrera"
];

// Eliminar las páginas demostrativas
paginasRetiradas.forEach(nombre => {
    const pagina = $(`#page-${nombre}`);
    if (pagina) pagina.remove();
});

// Retirar enlaces de navegación de las páginas eliminadas
$$("[data-page]").forEach(elemento => {
    if (paginasRetiradas.includes(elemento.dataset.page)) {
        elemento.remove();
    }
});

// Retirar tarjetas y botones que conducen a módulos eliminados
$$("[data-go]").forEach(elemento => {
    if (paginasRetiradas.includes(elemento.dataset.go)) {
        elemento.remove();
    }
});

// Retirar cualquier bloque de preguntas de demostración
const evaluaciones = $("#page-evaluaciones");

if (evaluaciones) {
    evaluaciones.innerHTML = `
        <div style="
            max-width: 850px;
            margin: 30px auto;
            padding: 35px 25px;
            text-align: center;
            border-radius: 20px;
            background: var(--panel, #141b35);
            color: var(--text, #ffffff);
        ">
            <div style="font-size: 42px; margin-bottom: 15px;">📚</div>

            <h2 style="margin-bottom: 12px;">
                Evaluaciones académicas
            </h2>

            <p style="
                max-width: 600px;
                margin: 0 auto 25px;
                line-height: 1.7;
                opacity: .8;
            ">
                Aquí podrás acceder a las evaluaciones oficiales
                de Matemáticas, Comprensión lectora,
                Razonamiento lógico y Lenguaje.
                Las preguntas serán incorporadas posteriormente
                desde el panel administrativo.
            </p>

            <a href="/estudiante" style="
                display: inline-block;
                padding: 13px 25px;
                border-radius: 10px;
                background: #536dfe;
                color: white;
                text-decoration: none;
                font-weight: 700;
            ">
                Ir a mis evaluaciones →
            </a>

            <p style="
                margin-top: 20px;
                font-size: 13px;
                opacity: .65;
            ">
                Curso: 6to de secundaria · Gestión 2026
            </p>
        </div>
    `;
}

// Sustituir las estadísticas ficticias por el acceso al progreso real
const progreso = $("#page-progreso");

if (progreso) {
    progreso.innerHTML = `
        <div style="
            max-width: 850px;
            margin: 30px auto;
            padding: 35px 25px;
            text-align: center;
            border-radius: 20px;
            background: var(--panel, #141b35);
            color: var(--text, #ffffff);
        ">
            <div style="font-size: 42px; margin-bottom: 15px;">📊</div>

            <h2 style="margin-bottom: 12px;">
                Mi progreso académico
            </h2>

            <p style="
                max-width: 600px;
                margin: 0 auto 25px;
                line-height: 1.7;
                opacity: .8;
            ">
                Consulta tus resultados, calificaciones,
                evaluaciones realizadas y rendimiento
                académico desde tu panel de estudiante.
            </p>

            <a href="/estudiante" style="
                display: inline-block;
                padding: 13px 25px;
                border-radius: 10px;
                background: #536dfe;
                color: white;
                text-decoration: none;
                font-weight: 700;
            ">
                Ver mi progreso →
            </a>
        </div>
    `;
}

// Navegación principal
function go(page) {
    const destino = $(`#page-${page}`);

    if (!destino) return;

    $$(".page").forEach(pagina => {
        pagina.classList.toggle("active", pagina === destino);
    });

    $$(".nav-link").forEach(enlace => {
        enlace.classList.toggle(
            "active",
            enlace.dataset.page === page
        );
    });

    const breadcrumb = $("#pageCrumb");

    if (breadcrumb) {
        breadcrumb.textContent = pageNames[page] || "Inicio";
    }

    const sidebar = $("#sidebar");

    if (sidebar) {
        sidebar.classList.remove("open");
    }

    window.scrollTo({
        top: 0,
        behavior: "smooth"
    });
}

// Enlaces del menú
$$("[data-page]").forEach(enlace => {
    enlace.addEventListener("click", () => {
        go(enlace.dataset.page);
    });
});

// Botones que conducen a otras secciones
$$("[data-go]").forEach(boton => {
    boton.addEventListener("click", () => {
        go(boton.dataset.go);
    });
});

// Menú para dispositivos móviles
const mobileMenu = $("#mobileMenu");

if (mobileMenu) {
    mobileMenu.addEventListener("click", () => {
        const sidebar = $("#sidebar");

        if (sidebar) {
            sidebar.classList.toggle("open");
        }
    });
}

// Mostrar la página inicial
go("inicio");
