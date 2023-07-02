package com.example.proyecto_danp.selector

sealed class PantallasExistentes(val route: String) {
    object SwitcherScreen: PantallasExistentes("switcher-scr")
    object ConsultaScreen: PantallasExistentes("consulta-scr")
    object FormularioScreen: PantallasExistentes("formulario-scr")
}
