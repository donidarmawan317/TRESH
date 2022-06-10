import { Sequelize } from "sequelize";
const db = new Sequelize('auth_db', 'root', 'admin123456', {
    host: '34.126.87.100',
    dialect: "mysql"
});

export default db;