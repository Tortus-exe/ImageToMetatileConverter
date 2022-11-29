libpng←"/usr/lib/libpng16.so.16"
png_path←⊑⎊{0=≠𝕩?•Out "no file!"⋄•Exit @}•args

{¬•file.Exists 𝕩?•Out"no libpng available at "∾𝕩∾"!"⋄•Exit@;@} libpng
{¬•file.Exists 𝕩?•Out "file doesn't exist!"⋄•Exit @;@} png_path
{'f'≢•file.Type 𝕩?•Out "not a file!"⋄•Exit @;@} png_path

ToDigits←16{⌽𝕗|⌊∘÷⟜𝕗⍟(↕1+·⌊𝕗⋆⁼1⌈⊢)}
Hex←{2=≠𝕩?𝕩;'0'∾𝕩}⊏⟜(∾"0A"+⟜↕¨10‿26)
Unwords←∾∾⟜' '¨

Read←{
	raw←(⥊Hex∘ToDigits)¨ @-˜•FBytes 𝕩
	⟨"89","50","4E","47"⟩≢4↑raw?•Out "not a PNG!"⋄•Exit;

}

•Out Unwords Read png_path

