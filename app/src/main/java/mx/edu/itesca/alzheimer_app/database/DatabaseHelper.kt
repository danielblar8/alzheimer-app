package mx.edu.itesca.alzheimer_app.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import mx.edu.itesca.alzheimer_app.model.Evaluacion
import mx.edu.itesca.alzheimer_app.model.Paciente

class DatabaseHelper(context: Context) : SQLiteOpenHelper(
    context, DATABASE_NAME, null, DATABASE_VERSION
) {

    companion object {
        const val DATABASE_NAME = "alzheimer.db"
        const val DATABASE_VERSION = 1

        // Tabla pacientes
        const val TABLE_PACIENTES = "pacientes"
        const val COL_PAC_ID = "id"
        const val COL_PAC_NOMBRE = "nombre"
        const val COL_PAC_APELLIDO = "apellido"
        const val COL_PAC_FECHA_NAC = "fecha_nacimiento"
        const val COL_PAC_GENERO = "genero"
        const val COL_PAC_TELEFONO = "telefono"

        // Tabla evaluaciones
        const val TABLE_EVALUACIONES = "evaluaciones"
        const val COL_EVAL_ID = "id"
        const val COL_EVAL_PACIENTE_ID = "paciente_id"
        const val COL_EVAL_INSTRUMENTO = "instrumento"
        const val COL_EVAL_PUNTAJE = "puntaje"
        const val COL_EVAL_PUNTAJE_MAX = "puntaje_maximo"
        const val COL_EVAL_FECHA = "fecha"
        const val COL_EVAL_OBSERVACIONES = "observaciones"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createPacientes = """
            CREATE TABLE $TABLE_PACIENTES (
                $COL_PAC_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_PAC_NOMBRE TEXT NOT NULL,
                $COL_PAC_APELLIDO TEXT NOT NULL,
                $COL_PAC_FECHA_NAC TEXT NOT NULL,
                $COL_PAC_GENERO TEXT NOT NULL,
                $COL_PAC_TELEFONO TEXT NOT NULL
            )
        """.trimIndent()

        val createEvaluaciones = """
            CREATE TABLE $TABLE_EVALUACIONES (
                $COL_EVAL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_EVAL_PACIENTE_ID INTEGER NOT NULL,
                $COL_EVAL_INSTRUMENTO TEXT NOT NULL,
                $COL_EVAL_PUNTAJE INTEGER NOT NULL,
                $COL_EVAL_PUNTAJE_MAX INTEGER NOT NULL,
                $COL_EVAL_FECHA TEXT NOT NULL,
                $COL_EVAL_OBSERVACIONES TEXT,
                FOREIGN KEY ($COL_EVAL_PACIENTE_ID) REFERENCES $TABLE_PACIENTES($COL_PAC_ID)
            )
        """.trimIndent()

        db.execSQL(createPacientes)
        db.execSQL(createEvaluaciones)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_EVALUACIONES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PACIENTES")
        onCreate(db)
    }

    // ─── CRUD PACIENTES ───────────────────────────────────────────

    fun insertarPaciente(paciente: Paciente): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_PAC_NOMBRE, paciente.nombre)
            put(COL_PAC_APELLIDO, paciente.apellido)
            put(COL_PAC_FECHA_NAC, paciente.fechaNacimiento)
            put(COL_PAC_GENERO, paciente.genero)
            put(COL_PAC_TELEFONO, paciente.telefono)
        }
        return db.insert(TABLE_PACIENTES, null, values)
    }

    fun obtenerPacientes(): List<Paciente> {
        val lista = mutableListOf<Paciente>()
        val db = readableDatabase
        val cursor = db.query(TABLE_PACIENTES, null, null, null, null, null, "$COL_PAC_APELLIDO ASC")
        while (cursor.moveToNext()) {
            lista.add(
                Paciente(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_PAC_ID)),
                    nombre = cursor.getString(cursor.getColumnIndexOrThrow(COL_PAC_NOMBRE)),
                    apellido = cursor.getString(cursor.getColumnIndexOrThrow(COL_PAC_APELLIDO)),
                    fechaNacimiento = cursor.getString(cursor.getColumnIndexOrThrow(COL_PAC_FECHA_NAC)),
                    genero = cursor.getString(cursor.getColumnIndexOrThrow(COL_PAC_GENERO)),
                    telefono = cursor.getString(cursor.getColumnIndexOrThrow(COL_PAC_TELEFONO))
                )
            )
        }
        cursor.close()
        return lista
    }

    fun eliminarPaciente(id: Int): Int {
        val db = writableDatabase
        return db.delete(TABLE_PACIENTES, "$COL_PAC_ID=?", arrayOf(id.toString()))
    }

    // ─── CRUD EVALUACIONES ────────────────────────────────────────

    fun insertarEvaluacion(evaluacion: Evaluacion): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_EVAL_PACIENTE_ID, evaluacion.pacienteId)
            put(COL_EVAL_INSTRUMENTO, evaluacion.instrumento)
            put(COL_EVAL_PUNTAJE, evaluacion.puntaje)
            put(COL_EVAL_PUNTAJE_MAX, evaluacion.puntajeMaximo)
            put(COL_EVAL_FECHA, evaluacion.fecha)
            put(COL_EVAL_OBSERVACIONES, evaluacion.observaciones)
        }
        return db.insert(TABLE_EVALUACIONES, null, values)
    }

    fun obtenerEvaluacionesPorPaciente(pacienteId: Int): List<Evaluacion> {
        val lista = mutableListOf<Evaluacion>()
        val db = readableDatabase
        val cursor = db.query(
            TABLE_EVALUACIONES, null,
            "$COL_EVAL_PACIENTE_ID=?", arrayOf(pacienteId.toString()),
            null, null, "$COL_EVAL_FECHA DESC"
        )
        while (cursor.moveToNext()) {
            lista.add(
                Evaluacion(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_EVAL_ID)),
                    pacienteId = cursor.getInt(cursor.getColumnIndexOrThrow(COL_EVAL_PACIENTE_ID)),
                    instrumento = cursor.getString(cursor.getColumnIndexOrThrow(COL_EVAL_INSTRUMENTO)),
                    puntaje = cursor.getInt(cursor.getColumnIndexOrThrow(COL_EVAL_PUNTAJE)),
                    puntajeMaximo = cursor.getInt(cursor.getColumnIndexOrThrow(COL_EVAL_PUNTAJE_MAX)),
                    fecha = cursor.getString(cursor.getColumnIndexOrThrow(COL_EVAL_FECHA)),
                    observaciones = cursor.getString(cursor.getColumnIndexOrThrow(COL_EVAL_OBSERVACIONES))
                )
            )
        }
        cursor.close()
        return lista
    }
}