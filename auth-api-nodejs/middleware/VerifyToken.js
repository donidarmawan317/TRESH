import jwt from "jsonwebtoken";

export const verifyToken = (request, response, next) => {
    const authHeader = request.headers['authorization'];
    const token = authHeader && authHeader.split(' ')[1];
    if (token == null) return response.sendStatus(401);
    jwt.verify(token, process.env.ACCESS_TOKEN_SECRET, (err, decoded) => {
        if (err) return response.sendStatus(403);
        request.email = decoded.email;
        next();
    })
}