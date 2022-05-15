# CustomWardrobe

## GPLv3.0

>Copyright (C) 2022  Git-a-Live
>
>This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.
>
>This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
>
>You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.

## 项目简介 | Introduction

本项目基于 [GioSDA/Wardrobe](https://github.com/GioSDA/Wardrobe) ，采用Kotlin全面重构开发，为 DripDrop Minecraft 服务器进行专门定制。
之所以没有fork源项目，而是新建仓库并全面重构开发，是因为源项目没有提交任何跟编译环境相关的文件，并且代码十分混乱，难以在此基础上继续开展工作。为了能够更好地进行开发维护的工作，
只能创建新项目，在审查源项目代码的同时，对其进行重构开发。

Based on [GioSDA/Wardrobe](https://github.com/GioSDA/Wardrobe), this project is refactored and redeveloped completely with Kotlin,
and specially customized for Minecraft server DripDrop. I didn't fork from the source project and determined to refactor it by creating a new repository because I have to. 
The source project doesn't contain any file that relative with compiling or building, such as `build.gradle`.
But actually, it seems easier to be handled when being compared with the horrible code style.
Jesus, code style of the source project is so terrible that I have no idea what other developers' feelings are when they
try to review the source code. By the way, I should pay respect to GioSDA and [Biplon](https://github.com/Biplon/Wardrobe)
for their work and patience.

> 如果想了解最原始的项目代码有多令人震撼，请访问这个仓库：https://github.com/LucFr3/Wardrobe
> 目前看来作者并不打算按照编码规范做出改进，大概是因为开发水平有限，无力重构整个项目。
> 
> You can go to the repository https://github.com/LucFr3/Wardrobe to learn how horrible the code style is
> in the original project. The developer looks unable to refactor the project by self, because the guy rejected
> some suggestions that ask him / her to follow Java coding rules while programming.