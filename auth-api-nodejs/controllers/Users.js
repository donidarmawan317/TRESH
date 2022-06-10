import Users from "../models/UserModel.js";
import bcrypt from "bcrypt";
import jwt from "jsonwebtoken";

export const getUsers = async(request, response) => {
    try {
        const users = await Users.findAll({
            attributes: ['id', 'name', 'email']
        });
        response.json(users);
    } catch (error) {
        console.log(error);

    }
}

export const Register = async(request, response) => {
    const { name, email, password, confPassword } = request.body;
    if (password !== confPassword) return response.status(400).json({ msg: "Password and Confirm Password don't match" });
  const salt = await bcrypt.genSalt();
  const hashPassword = await bcrypt.hash(password, salt);
    try {
        await Users.create({
            name: name,
            email: email,
            password: hashPassword
        });
        response.json({ msg: "register successful" });
    } catch (error) {
        console.log(error);

    }
}

export const Login = async(request, response) => {
    try {
        const user = await Users.findAll({
            where: {
                email: request.body.email
            }
        });
       const match = await bcrypt.compare(request.body.password, user[0].password);
        if (!match) return response.status(400).json({ msg: "Wrong Password" });
        const userId = user[0].id;
        const name = user[0].name;
        const email = user[0].email;
        const accessToken = jwt.sign({ userId, name, email }, process.env.ACCESS_TOKEN_SECRET, {
            expiresIn: '30s'

        });
        const refreshToken = jwt.sign({ userId, name, email }, process.env.REFRESH_TOKEN_SECRET, {
            expiresIn: '1d'
        });
        await Users.update({ refresh_token: refreshToken }, {
            where: {
                id: userId
            }
        });
        response.cookie('refreshToken', refreshToken, {
            httpOnly: true,
            maxAge: 24 * 60 * 60 * 1000
                // secure:true
        });
        response.json({ accessToken });

    } catch (error) {
        response.status(404).json({ msg: "email not found" });
    }
}

export const Logout = async(request, response) => {
    const refreshToken = request.cookies.refreshToken;
    if (!refreshToken) return response.sendStatus(204);
    const user = await Users.findAll({
        where: {
            refresh_token: refreshToken
        }
    });
    if (!user[0]) return response.sendStatus(204);
    const userId = user[0].id;
    await Users.update({ refresh_token: null }, {
        where: {
            id: userId
        }
    });
    response.clearCookie('refreshToken');
    return response.sendStatus(200);
}