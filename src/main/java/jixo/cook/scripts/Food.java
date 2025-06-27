package jixo.cook.scripts;

public interface Food {

    String getName();
    void setName(String name);

    String getImageUrl();
    void setImageUrl(String imageUrl);

    String getEnergy();
    void setEnergy(String energy);

    String getSugar();
    void setSugar(String sugar);

    String getSaturatedFat();
    void setSaturatedFat(String saturatedFat);

    String getSalt();
    void setSalt(String salt);

    String getProteins();
    void setProteins(String proteins);

    String getFiber();
    void setFiber(String fiber);
}